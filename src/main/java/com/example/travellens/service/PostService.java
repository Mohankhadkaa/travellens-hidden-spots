package com.example.travellens.service;

import com.example.travellens.entity.Post;
import com.example.travellens.entity.User;
import com.example.travellens.repository.PostRepository;
import com.example.travellens.repository.UserRepository;
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

    public PostService(PostRepository postRepository, UserRepository userRepository,
                       Optional<CloudinaryImageService> cloudinaryImageService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.cloudinaryImageService = cloudinaryImageService;
    }

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public List<Post> getPostsByUser(User user) {
        return postRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Transactional
    public Post createPost(String title, String location, String category,
                           String description, MultipartFile imageFile, User user) {
        User managedUser = userRepository.getReferenceById(user.getId());
        Post post = new Post(title, location, category, description, null, managedUser);

        if (imageFile != null && !imageFile.isEmpty() && cloudinaryImageService.isPresent()) {
            CloudinaryImageService.UploadResult result = cloudinaryImageService.get().upload(imageFile);
            if (result != null) {
                post.setImageUrl(result.url());
                post.setImagePublicId(result.publicId());
            }
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

        if (imageFile != null && !imageFile.isEmpty() && cloudinaryImageService.isPresent()) {
            CloudinaryImageService.UploadResult result = cloudinaryImageService.get().upload(imageFile);
            if (result != null) {
                String oldPublicId = post.getImagePublicId();
                post.setImageUrl(result.url());
                post.setImagePublicId(result.publicId());
                if (oldPublicId != null) {
                    cloudinaryImageService.get().delete(oldPublicId);
                }
            }
        }

        return postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if (post.getImagePublicId() != null) {
            cloudinaryImageService.ifPresent(svc -> svc.delete(post.getImagePublicId()));
        }
        postRepository.delete(post);
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
