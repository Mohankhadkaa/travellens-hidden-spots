package com.example.travellens.service;

import com.example.travellens.entity.Post;
import com.example.travellens.entity.User;
import com.example.travellens.repository.PostRepository;
import com.example.travellens.repository.UserRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PostService {

    private static final Logger log = LoggerFactory.getLogger(PostService.class);

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final Optional<CloudinaryImageService> cloudinaryImageService;
    private final LocalImageService localImageService;

    public PostService(PostRepository postRepository, UserRepository userRepository,
                       Optional<CloudinaryImageService> cloudinaryImageService,
                       LocalImageService localImageService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.cloudinaryImageService = cloudinaryImageService;
        this.localImageService = localImageService;
    }

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Optional<Post> getPostById(Long id) {
        return postRepository.findByIdWithUser(id);
    }

    public List<Post> getPostsByUser(User user) {
        return postRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Transactional
    public Post createPost(String title, String location, String category,
                           String description, MultipartFile imageFile, User user) {
        User managedUser = userRepository.getReferenceById(user.getId());
        Post post = new Post(title, location, category, description, null, managedUser);

        if (imageFile != null && !imageFile.isEmpty()) {
            saveImage(post, imageFile);
        }

        return postRepository.save(post);
    }

    @Transactional
    public Post updatePost(Long id, String title, String location, String category,
                           String description, MultipartFile imageFile) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setTitle(title);
        post.setLocation(location);
        post.setCategory(category);
        post.setDescription(description);

        if (imageFile != null && !imageFile.isEmpty()) {
            String oldPublicId = post.getImagePublicId();
            String oldImageName = post.getImageName();
            saveImage(post, imageFile);
            deleteImage(oldPublicId, oldImageName);
        }

        return postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        deleteImage(post.getImagePublicId(), post.getImageName());
        postRepository.delete(post);
    }

    private void saveImage(Post post, MultipartFile imageFile) {
        if (cloudinaryImageService.isPresent()) {
            CloudinaryImageService.UploadResult result = cloudinaryImageService.get().upload(imageFile);
            if (result != null) {
                post.setImageName(null);
                post.setImageUrl(result.url());
                post.setImagePublicId(result.publicId());
                post.setImageData(null);
                post.setImageContentType(null);
            }
            return;
        }

        try {
            post.setImageName(imageFile.getOriginalFilename());
            post.setImageUrl(null);
            post.setImagePublicId(null);
            post.setImageData(imageFile.getBytes());
            post.setImageContentType(imageFile.getContentType());
        } catch (IOException e) {
            throw new RuntimeException("Could not save uploaded image", e);
        }
    }

    private void deleteImage(String publicId, String imageName) {
        if (publicId != null) {
            cloudinaryImageService.ifPresent(svc -> svc.delete(publicId));
        } else {
            localImageService.delete(imageName);
        }
    }

    public List<Post> searchPosts(String keyword, String category) {
        String trimmedKeyword = keyword != null ? keyword.trim() : "";
        String trimmedCategory = category != null ? category.trim() : "";
        if (trimmedKeyword.isEmpty() && (trimmedCategory.isEmpty() || trimmedCategory.equals("All Categories"))) {
            return postRepository.findAllByOrderByCreatedAtDesc();
        }
        return postRepository.searchPosts(
                trimmedKeyword.isEmpty() ? null : trimmedKeyword,
                trimmedCategory.isEmpty() || trimmedCategory.equals("All Categories") ? null : trimmedCategory
        );
    }

    public boolean isOwnerOrAdmin(Post post, String userEmail, boolean isAdmin) {
        return post.getUser().getEmail().equals(userEmail) || isAdmin;
    }
}
