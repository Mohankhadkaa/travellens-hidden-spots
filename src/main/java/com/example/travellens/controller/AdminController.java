package com.example.travellens.controller;

import com.example.travellens.entity.Post;
import com.example.travellens.entity.User;
import com.example.travellens.service.PostService;
import com.example.travellens.service.UserService;
import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final UserService userService;
    private final PostService postService;

    public AdminController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping
    public String dashboard(Model model) {
        var users = userService.findAllUsers();
        model.addAttribute("totalUsers", users.size());
        model.addAttribute("totalUserAccounts", users.stream().filter(u -> "USER".equals(u.getRole())).count());
        model.addAttribute("totalAdminAccounts", users.stream().filter(u -> "ADMIN".equals(u.getRole())).count());
        model.addAttribute("totalPosts", postService.getAllPosts().size());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String listUsers(Model model, Principal principal) {
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("currentEmail", principal.getName());
        return "admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, Principal principal,
                             RedirectAttributes redirectAttributes) {
        User target = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (target.getEmail().equals(principal.getName())) {
            redirectAttributes.addFlashAttribute("error", "You cannot delete your own account.");
            return "redirect:/admin/users";
        }
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "User " + target.getEmail() + " deleted successfully.");
        } catch (Exception e) {
            log.error("Failed to delete user {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Failed to delete user " + target.getEmail() + ".");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/posts")
    public String listPosts(Model model) {
        model.addAttribute("posts", postService.getAllPosts());
        return "admin/posts";
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            postService.deletePost(id);
            redirectAttributes.addFlashAttribute("success", "Post deleted successfully.");
        } catch (Exception e) {
            log.error("Failed to delete post {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Failed to delete post.");
        }
        return "redirect:/admin/posts";
    }
}
