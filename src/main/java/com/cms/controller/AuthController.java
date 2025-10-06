package com.cms.controller;

import com.cms.model.User;
import com.cms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // Ensure a user object is always present in the model for the form
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        try {
            // The service layer will handle setting the role to "student"
            userService.registerStudent(user);
            // Add a success message to be displayed on the registration page after redirect
            redirectAttributes.addFlashAttribute("message", "Registration completed successfully! Details have been mailed to you.");
        } catch (Exception e) {
            // A more specific error message would be better in a real application
            redirectAttributes.addFlashAttribute("error", "An error occurred during registration. The email might already be in use.");
            // Add the user object back to the model to repopulate the form
            redirectAttributes.addFlashAttribute("user", user);
        }
        return "redirect:/register";
    }
}

