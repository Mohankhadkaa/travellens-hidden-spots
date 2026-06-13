package com.example.travellens.controller;

import com.example.travellens.entity.Post;
import com.example.travellens.entity.User;
import com.example.travellens.service.CloudinaryImageService;
import com.example.travellens.service.PostService;
import com.example.travellens.service.UserService;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final boolean cloudinaryConfigured;

    public PostController(PostService postService, UserService userService,
                          Optional<CloudinaryImageService> cloudinaryImageService) {
        this.postService = postService;
        this.userService = userService;
        this.cloudinaryConfigured = cloudinaryImageService.isPresent();
    }

    @ModelAttribute
    public void addCloudinaryConfigured(Model model) {
        model.addAttribute("cloudinaryConfigured", cloudinaryConfigured);
    }

    @GetMapping
    public String listPosts(Model model) {
        model.addAttribute("posts", postService.getAllPosts());
        model.addAttribute("isSearch", false);
        return "posts/list";
    }

    @GetMapping("/search")
    public String searchPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "") String category,
            Model model) {
        List<Post> results = postService.searchPosts(keyword, category);
        model.addAttribute("posts", results);
        model.addAttribute("keyword", keyword != null ? keyword.trim() : "");
        model.addAttribute("category", category);
        model.addAttribute("resultCount", results.size());
        model.addAttribute("isSearch", true);
        return "posts/list";
    }

    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model, Principal principal) {
        Post post = postService.getPostById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        model.addAttribute("post", post);
        if (principal != null) {
            model.addAttribute("currentUserEmail", principal.getName());
        }
        return "posts/detail";
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> viewPostImage(@PathVariable Long id) {
        Post post = postService.getPostById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        byte[] imageData = post.getImageData();
        if (imageData == null || imageData.length == 0) {
            return ResponseEntity.notFound().build();
        }

        String contentType = post.getImageContentType() != null ? post.getImageContentType() : MediaType.IMAGE_JPEG_VALUE;
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000")
                .contentType(MediaType.parseMediaType(contentType))
                .body(imageData);
    }

    @GetMapping("/new")
    public String createForm() {
        return "posts/create";
    }

    @PostMapping
    public String createSubmit(
            @RequestParam String title,
            @RequestParam String location,
            @RequestParam String category,
            @RequestParam String description,
            @RequestParam(value = "image", required = false) MultipartFile image,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (title.isBlank()) {
            model.addAttribute("error", "Title is required");
            return "posts/create";
        }
        if (location.isBlank()) {
            model.addAttribute("error", "Location is required");
            return "posts/create";
        }
        if (category.isBlank()) {
            model.addAttribute("error", "Category is required");
            return "posts/create";
        }
        if (description.isBlank()) {
            model.addAttribute("error", "Description is required");
            return "posts/create";
        }

        if (image != null && !image.isEmpty()) {
            String contentType = image.getContentType();
            if (contentType == null || !Set.of("image/jpeg", "image/png", "image/webp").contains(contentType)) {
                model.addAttribute("error", "Only JPG, PNG, and WEBP images are allowed");
                return "posts/create";
            }
            if (image.getSize() > 5 * 1024 * 1024) {
                model.addAttribute("error", "Image must be less than 5 MB");
                return "posts/create";
            }
        }

        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            Post post = postService.createPost(title, location, category, description, image, user);
            redirectAttributes.addFlashAttribute("success", "Post created successfully!");
            return "redirect:/posts/" + post.getId();
        } catch (Exception e) {
            model.addAttribute("error", "Failed to create post: " + e.getMessage());
            return "posts/create";
        }
    }

    @PostMapping("/new")
    public String createSubmitNew(
            @RequestParam String title,
            @RequestParam String location,
            @RequestParam String category,
            @RequestParam String description,
            @RequestParam(value = "image", required = false) MultipartFile image,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {
        return createSubmit(title, location, category, description, image, authentication, redirectAttributes, model);
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, Authentication authentication) {
        Post post = postService.getPostById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        checkAccess(post, authentication);
        model.addAttribute("post", post);
        return "posts/edit";
    }

    @PostMapping("/{id}/edit")
    public String editSubmit(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String location,
            @RequestParam String category,
            @RequestParam String description,
            @RequestParam(value = "image", required = false) MultipartFile image,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        Post post = postService.getPostById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        checkAccess(post, authentication);

        if (title.isBlank()) {
            model.addAttribute("error", "Title is required");
            model.addAttribute("post", post);
            return "posts/edit";
        }
        if (location.isBlank()) {
            model.addAttribute("error", "Location is required");
            model.addAttribute("post", post);
            return "posts/edit";
        }
        if (category.isBlank()) {
            model.addAttribute("error", "Category is required");
            model.addAttribute("post", post);
            return "posts/edit";
        }
        if (description.isBlank()) {
            model.addAttribute("error", "Description is required");
            model.addAttribute("post", post);
            return "posts/edit";
        }

        if (image != null && !image.isEmpty()) {
            String contentType = image.getContentType();
            if (contentType == null || !Set.of("image/jpeg", "image/png", "image/webp").contains(contentType)) {
                model.addAttribute("error", "Only JPG, PNG, and WEBP images are allowed");
                model.addAttribute("post", post);
                return "posts/edit";
            }
            if (image.getSize() > 5 * 1024 * 1024) {
                model.addAttribute("error", "Image must be less than 5 MB");
                model.addAttribute("post", post);
                return "posts/edit";
            }
        }

        try {
            postService.updatePost(id, title, location, category, description, image);
            redirectAttributes.addFlashAttribute("success", "Post updated successfully!");
            return "redirect:/posts/" + id;
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update post: " + e.getMessage());
            model.addAttribute("post", post);
            return "posts/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteSubmit(@PathVariable Long id, Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        Post post = postService.getPostById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        checkAccess(post, authentication);
        postService.deletePost(id);
        redirectAttributes.addFlashAttribute("success", "Post deleted successfully!");
        return "redirect:/posts";
    }

    private void checkAccess(Post post, Authentication authentication) {
        String email = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!postService.isOwnerOrAdmin(post, email, isAdmin)) {
            throw new AccessDeniedException("Not authorized");
        }
    }
}
