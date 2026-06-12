package com.example.travellens.service;

import com.example.travellens.entity.Post;
import com.example.travellens.entity.User;
import com.example.travellens.repository.PostRepository;
import com.example.travellens.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PostRepository postRepository;
    private final Optional<CloudinaryImageService> cloudinaryImageService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       PostRepository postRepository,
                       Optional<CloudinaryImageService> cloudinaryImageService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.postRepository = postRepository;
        this.cloudinaryImageService = cloudinaryImageService;
    }

    @Transactional
    public User registerUser(String username, String email, String password) {
        User user = new User(username, email, passwordEncoder.encode(password));
        user.setRole("USER");
        return userRepository.save(user);
    }

    public java.util.Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional
    public void createAdminUserIfNotExists() {
        String adminEmail = System.getenv("ADMIN_EMAIL");
        String adminPassword = System.getenv("ADMIN_PASSWORD");

        if (adminEmail == null || adminPassword == null) {
            adminEmail = "admin@travellens.com";
            adminPassword = "AdminPass123!";
            log.info("Using default admin credentials (admin@travellens.com / AdminPass123!)");
        }

        Optional<User> existing = userRepository.findByEmail(adminEmail);

        if (existing.isPresent()) {
            User admin = existing.get();
            admin.setRole("ADMIN");
            admin.setPassword(passwordEncoder.encode(adminPassword));
            userRepository.save(admin);
            log.info("Admin account updated successfully.");
        } else {
            User admin = new User("admin", adminEmail, passwordEncoder.encode(adminPassword));
            admin.setRole("ADMIN");
            userRepository.save(admin);
            log.info("Admin account created successfully.");
        }
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Post> posts = postRepository.findByUserOrderByCreatedAtDesc(user);
        for (Post post : posts) {
            if (post.getImagePublicId() != null) {
                cloudinaryImageService.ifPresent(svc -> svc.delete(post.getImagePublicId()));
            }
        }
        postRepository.deleteAll(posts);
        userRepository.delete(user);
        log.info("Deleted user {} and {} posts", user.getEmail(), posts.size());
    }
}
