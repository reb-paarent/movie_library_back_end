package com.example.movies.web;

import java.security.Principal;

import jakarta.validation.Valid;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.movies.domain.*;

@Controller
public class UserController {

    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection
    public UserController(AppUserRepository appUserReporitory, PasswordEncoder passwordEncoder) {
        this.repository = appUserReporitory;
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping(value = "/login")
    public String login() {
        return "login";
    }

    @RequestMapping(value = "signup")
    public String addStudent(Model model) {
        model.addAttribute("signupform", new SignupForm());
        return "signup";
    }

    /**
     * Create new user
     * Check if user already exists & form validation
     * 
     * @param signupForm
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "saveuser", method = RequestMethod.POST)
    public String save(@Valid @ModelAttribute("signupform") SignupForm signupForm, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) { // validation errors
            if (signupForm.getPassword().equals(signupForm.getPasswordCheck())) { // Check password match
                String hashPwd = passwordEncoder.encode(signupForm.getPassword());

                AppUser newUser = new AppUser();
                newUser.setPasswordHash(hashPwd);
                newUser.setUsername(signupForm.getUsername());
                newUser.setEmail(signupForm.getEmail());
                newUser.setRole("USER");
                if (repository.findByUsername(signupForm.getUsername()) == null) { // Check if user exists
                    repository.save(newUser);
                } else {
                    bindingResult.rejectValue("username", "err.username", "Username already exists");
                    return "signup";
                }
            } else {
                bindingResult.rejectValue("passwordCheck", "err.passCheck", "Passwords do not match");
                return "signup";
            }
        } else {
            return "signup";
        }
        return "redirect:/login";
    }

    @PostMapping("/usersettings/password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes) {
        AppUser user = repository.findByUsername(principal.getName());
        if (user == null) {
            throw new IllegalStateException(
                    "User" + principal.getName() + "does not exist.");
        }
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            model.addAttribute("passwordError", "Current password is incorrect.");
            return "usersettings";
        }
        if (newPassword.length() < 12 || newPassword.length() > 30) {
            model.addAttribute("passwordError", "New password must be between 12 and 30 characters.");
            return "usersettings";
        }
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("passwordError", "New password and confirmation do not match.");
            return "usersettings";
        }
        if (confirmPassword.length() < 12 || confirmPassword.length() > 30) {
            model.addAttribute("passwordError", "New password must be between 12 and 30 characters.");
            return "usersettings";
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        repository.save(user);
        redirectAttributes.addFlashAttribute("passwordSuccess", "Password changed successfully.");
        return "redirect:/usersettings";
    }

}
