package com.example.travellens.controller;

import com.example.travellens.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/signup")
    public String signupForm() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signupSubmit(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (username.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            model.addAttribute("error", "All fields are required");
            return "signup";
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            model.addAttribute("error", "Please enter a valid email address");
            return "signup";
        }

        if (password.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters");
            return "signup";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "signup";
        }

        if (userService.existsByEmail(email)) {
            model.addAttribute("error", "Email is already registered");
            return "signup";
        }

        if (userService.existsByUsername(username)) {
            model.addAttribute("error", "Username is already taken");
            return "signup";
        }

        userService.registerUser(username, email, password);
        redirectAttributes.addFlashAttribute("success", "Registration successful! Please log in.");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }
}
