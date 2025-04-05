package com.ticketing.controller;

import com.ticketing.model.Ticket;
import com.ticketing.model.User;
import com.ticketing.service.TicketService;
import com.ticketing.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;
    private final TicketService ticketService;
    
    @GetMapping("/")
    public String home(Principal principal) {
        // Se l'utente è autenticato, reindirizza alla dashboard, altrimenti alla pagina di benvenuto
        return principal != null ? "redirect:/dashboard" : "welcome";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            // Ottieni l'autenticazione corrente
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null && authentication.isAuthenticated()) {
                // Ottieni il nome utente
                String username = authentication.getName();
                
                // Ottieni i dati completi dell'utente
                User currentUser = userService.getUserByUsername(username);
                
                // Aggiungi i dati dell'utente al modello
                model.addAttribute("user", currentUser);
                model.addAttribute("fullName", currentUser.getFirstName() + " " + currentUser.getLastName());
                model.addAttribute("roles", currentUser.getRoles());
                model.addAttribute("department", currentUser.getDepartment());
                
                // Debug: aggiungi tutti i dati dell'autenticazione
                model.addAttribute("authentication", authentication);
                
                return "dashboard";
            } else {
                // Se l'utente non è autenticato, reindirizza al login
                return "redirect:/login?error=notauthenticated";
            }
        } catch (Exception e) {
            // Log dell'errore
            e.printStackTrace();
            return "redirect:/login?error=dashboarderror&message=" + e.getMessage();
        }
    }
}
