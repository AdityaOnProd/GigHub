package com.app.gighub.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.gighub.models.User;
import com.app.gighub.services.UserService;

import jakarta.validation.Valid;  // ✅ Updated for Spring Boot 3+

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier("userValidator")
    private Validator userValidator;

    @GetMapping("/login")
    public String login() {
        return "login/login";
    }

    @RequestMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());  // Ensure an empty User object is passed
        return "login/register";
    }

    @PostMapping("/register")
    public ModelAndView doRegister(@Valid @ModelAttribute User user, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            // Collect error messages
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(e -> StringUtils.capitalize(e.getDefaultMessage()))
                    .collect(Collectors.toList());

            model.addAttribute("errorMessages", errors);
            model.addAttribute("user", user);
            return new ModelAndView("login/register", model.asMap());
        }

        userService.save(user);

        // Add success message and redirect to login page
        redirectAttributes.addFlashAttribute("successMessage", "Registration successful! You can now log in.");
        return new ModelAndView("redirect:/login");
    }
}
