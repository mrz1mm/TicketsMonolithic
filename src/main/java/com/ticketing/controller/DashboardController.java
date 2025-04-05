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
    public String dashboard(Model model, Principal principal) {
        User currentUser = userService.getUserByUsername(principal.getName());
        
        // Get recent tickets
        List<Ticket> recentTickets = ticketService.findTop10ByOrderByCreatedAtDesc();
        
        // Count tickets by status
        Map<Ticket.TicketStatus, Long> ticketStatusCounts = new HashMap<>();
        for (Ticket.TicketStatus status : Ticket.TicketStatus.values()) {
            ticketStatusCounts.put(status, ticketService.countByStatus(status));
        }
        
        boolean isAdmin = currentUser.getRoles().stream().anyMatch(r -> r.getName().equals("ADMIN"));
        boolean isSupport = currentUser.getRoles().stream().anyMatch(r -> r.getName().equals("SUPPORT"));
        
        model.addAttribute("user", currentUser);
        model.addAttribute("recentTickets", recentTickets);
        model.addAttribute("ticketStatusCounts", ticketStatusCounts);
        
        // Add assigned tickets count if user is support staff
        if (isSupport || isAdmin) {
            Long assignedTicketsCount = ticketService.countByAssignedTo(currentUser);
            model.addAttribute("assignedTicketsCount", assignedTicketsCount);
        }
        
        return "dashboard";
    }
}
