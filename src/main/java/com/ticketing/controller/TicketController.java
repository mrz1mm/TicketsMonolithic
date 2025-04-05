package com.ticketing.controller;

import com.ticketing.dto.TicketDto;
import com.ticketing.dto.TicketReplyDto;
import com.ticketing.model.Ticket;
import com.ticketing.model.TicketAttachment;
import com.ticketing.model.User;
import com.ticketing.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final UserService userService;
    private final DepartmentService departmentService;
    private final CategoryService categoryService;

    @GetMapping
    public String listTickets(Model model, 
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             Principal principal) {
        
        User currentUser = userService.getUserByUsername(principal.getName());
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        // Different views based on user roles
        boolean isAdmin = currentUser.getRoles().stream().anyMatch(r -> r.getName().equals("ADMIN"));
        boolean isSupport = currentUser.getRoles().stream().anyMatch(r -> r.getName().equals("SUPPORT"));
        
        Page<Ticket> tickets;
        
        if (isAdmin) {
            // Admins can see all tickets
            tickets = ticketService.getAllTickets(pageRequest);
            model.addAttribute("viewType", "all");
        } else if (isSupport) {
            // Support users see tickets from their department
            tickets = ticketService.getTicketsByDepartment(currentUser.getDepartment().getId(), pageRequest);
            model.addAttribute("viewType", "department");
        } else {
            // Regular users see just their tickets
            tickets = ticketService.getTicketsByUser(currentUser.getId(), pageRequest);
            model.addAttribute("viewType", "own");
        }
        
        model.addAttribute("tickets", tickets);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", tickets.getTotalPages());
        model.addAttribute("statuses", Ticket.TicketStatus.values());
        
        return "tickets/list";
    }

    @GetMapping("/create")
    public String createTicketForm(Model model) {
        model.addAttribute("ticketDto", new TicketDto());
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("priorities", Ticket.TicketPriority.values());
        
        return "tickets/create";
    }

    @PostMapping("/create")
    public String createTicket(@Valid @ModelAttribute TicketDto ticketDto, 
                              BindingResult result, 
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("priorities", Ticket.TicketPriority.values());
            return "tickets/create";
        }
        
        Ticket ticket = ticketService.createTicket(ticketDto);
        return "redirect:/tickets/" + ticket.getId();
    }

    @GetMapping("/{id}")
    public String viewTicket(@PathVariable Long id, Model model) {
        Ticket ticket = ticketService.getTicketById(id);
        
        User currentUser = getCurrentUser();
        boolean canManageTicket = canUserManageTicket(ticket, currentUser);
        
        model.addAttribute("ticket", ticket);
        model.addAttribute("replies", ticketService.getTicketReplies(id));
        model.addAttribute("attachments", ticketService.getTicketAttachments(id));
        model.addAttribute("replyDto", new TicketReplyDto());
        model.addAttribute("statuses", Ticket.TicketStatus.values());
        model.addAttribute("priorities", Ticket.TicketPriority.values());
        model.addAttribute("canManageTicket", canManageTicket);
        
        // For assigning ticket
        if (canManageTicket) {
            model.addAttribute("supportUsers", userService.getSupportUsers());
        }
        
        return "tickets/view";
    }

    @PostMapping("/{id}/reply")
    public String addReply(@PathVariable Long id, 
                          @Valid @ModelAttribute TicketReplyDto replyDto,
                          BindingResult result,
                          RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Reply content is required");
            return "redirect:/tickets/" + id;
        }
        
        replyDto.setTicketId(id);
        ticketService.addReply(replyDto);
        
        return "redirect:/tickets/" + id;
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    public String assignTicket(@PathVariable Long id,
                              @RequestParam Long assignedToId,
                              RedirectAttributes redirectAttributes) {
        
        try {
            ticketService.assignTicket(id, assignedToId);
            redirectAttributes.addFlashAttribute("success", "Ticket assigned successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to assign ticket: " + e.getMessage());
        }
        
        return "redirect:/tickets/" + id;
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                              @RequestParam Ticket.TicketStatus status,
                              RedirectAttributes redirectAttributes) {
        
        try {
            Ticket ticket = ticketService.getTicketById(id);
            User currentUser = getCurrentUser();
            
            if (!canUserManageTicket(ticket, currentUser) && !ticket.getCreatedBy().equals(currentUser)) {
                redirectAttributes.addFlashAttribute("error", "You don't have permission to update this ticket");
                return "redirect:/tickets/" + id;
            }
            
            ticketService.updateTicketStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Ticket status updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update status: " + e.getMessage());
        }
        
        return "redirect:/tickets/" + id;
    }

    @PostMapping("/{id}/priority")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    public String updatePriority(@PathVariable Long id,
                                @RequestParam Ticket.TicketPriority priority,
                                RedirectAttributes redirectAttributes) {
        
        try {
            ticketService.updateTicketPriority(id, priority);
            redirectAttributes.addFlashAttribute("success", "Ticket priority updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update priority: " + e.getMessage());
        }
        
        return "redirect:/tickets/" + id;
    }

    @GetMapping("/download/{attachmentId}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long attachmentId) {
        try {
            TicketAttachment attachment = ticketService.getAttachmentById(attachmentId);
            
            Path filePath = Paths.get(attachment.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + attachment.getFileName() + "\"")
                    .body(resource);
            } else {
                throw new RuntimeException("Could not read the file");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error downloading file: " + e.getMessage());
        }
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.getUserByUsername(auth.getName());
    }

    private boolean canUserManageTicket(Ticket ticket, User user) {
        // Admins can manage all tickets
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ADMIN"));
        
        if (isAdmin) {
            return true;
        }
        
        // Support users can manage tickets in their department
        boolean isSupport = user.getRoles().stream()
                .anyMatch(r -> r.getName().equals("SUPPORT"));
        
        if (isSupport) {
            return user.getDepartment().equals(ticket.getDepartment());
        }
        
        // Assignees can manage their assigned tickets
        return user.equals(ticket.getAssignedTo());
    }
}
