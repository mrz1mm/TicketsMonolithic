package com.ticketing.controller;

import com.ticketing.dto.UserRegistrationDto;
import com.ticketing.service.DepartmentService;
import com.ticketing.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final DepartmentService departmentService;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(
        @Valid @ModelAttribute("registrationDto") UserRegistrationDto registrationDto,
        BindingResult result,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        // Validazione custom per verificare che le password corrispondano
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Le password non corrispondono");
        }
        
        // Se ci sono errori, ritorna al form di registrazione
        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "auth/register";
        }
        
        try {
            userService.registerNewUser(registrationDto);
            redirectAttributes.addFlashAttribute("successMessage", "Registrazione completata con successo! Accedi con le tue credenziali.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "auth/register";
        }
    }
}
