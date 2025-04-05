package com.ticketing.controller;

import com.ticketing.model.User;
import com.ticketing.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping
    public String viewProfile(Authentication authentication, Model model) {
        User user = userService.getUserByUsername(authentication.getName());
        model.addAttribute("user", user);
        return "profile/view";
    }
}
