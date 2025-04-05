package com.ticketing.service;

import com.ticketing.dto.TicketDto;
import com.ticketing.dto.TicketReplyDto;
import com.ticketing.model.*;
import com.ticketing.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final CategoryRepository categoryRepository;
    private final TicketReplyRepository ticketReplyRepository;
    private final TicketAttachmentRepository ticketAttachmentRepository;
    private final NotificationService notificationService;

    private static final String UPLOAD_DIR = "./uploads/";

    @Transactional
    public Ticket createTicket(TicketDto ticketDto) {
        User currentUser = getCurrentUser();
        Department department = departmentRepository.findById(ticketDto.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Ticket ticket = new Ticket();
        ticket.setTitle(ticketDto.getTitle());
        ticket.setDescription(ticketDto.getDescription());
        ticket.setStatus(Ticket.TicketStatus.OPEN);
        ticket.setPriority(ticketDto.getPriority() != null ? ticketDto.getPriority() : Ticket.TicketPriority.MEDIUM);
        ticket.setCreatedBy(currentUser);
        ticket.setDepartment(department);

        // Set categories if provided
        if (ticketDto.getCategoryIds() != null && !ticketDto.getCategoryIds().isEmpty()) {
            Set<Category> categories = new HashSet<>();
            for (Integer categoryId : ticketDto.getCategoryIds()) {
                categoryRepository.findById(categoryId).ifPresent(categories::add);
            }
            ticket.setCategories(categories);
        }

        Ticket savedTicket = ticketRepository.save(ticket);

        // Save attachments if any
        if (ticketDto.getAttachments() != null) {
            for (MultipartFile file : ticketDto.getAttachments()) {
                if (!file.isEmpty()) {
                    saveAttachment(file, savedTicket, currentUser);
                }
            }
        }

        // Notify department managers or admins about the new ticket
        notificationService.notifyNewTicket(savedTicket);

        return savedTicket;
    }

    @Transactional
    public TicketReply addReply(TicketReplyDto replyDto) {
        User currentUser = getCurrentUser();
        Ticket ticket = ticketRepository.findById(replyDto.getTicketId())
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        TicketReply reply = new TicketReply();
        reply.setContent(replyDto.getContent());
        reply.setUser(currentUser);
        reply.setTicket(ticket);
        reply.setInternalNote(replyDto.isInternalNote());

        TicketReply savedReply = ticketReplyRepository.save(reply);

        // Save attachments if any
        if (replyDto.getAttachments() != null) {
            for (MultipartFile file : replyDto.getAttachments()) {
                if (!file.isEmpty()) {
                    saveAttachment(file, ticket, currentUser);
                }
            }
        }

        // Update ticket status if it was OPEN or WAITING_FOR_RESPONSE
        if (ticket.getStatus() == Ticket.TicketStatus.OPEN) {
            ticket.setStatus(Ticket.TicketStatus.IN_PROGRESS);
            ticketRepository.save(ticket);
        } else if (currentUser.equals(ticket.getCreatedBy()) && 
                   ticket.getStatus() == Ticket.TicketStatus.WAITING_FOR_RESPONSE) {
            ticket.setStatus(Ticket.TicketStatus.IN_PROGRESS);
            ticketRepository.save(ticket);
        }

        // Notify relevant users about the new reply
        notificationService.notifyNewReply(savedReply);

        return savedReply;
    }

    @Transactional
    public Ticket assignTicket(Long ticketId, Long assignedToId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        User assignee = userRepository.findById(assignedToId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ticket.setAssignedTo(assignee);
        ticket.setStatus(Ticket.TicketStatus.ASSIGNED);
        
        Ticket updatedTicket = ticketRepository.save(ticket);
        
        // Notify the assignee
        notificationService.notifyTicketAssigned(updatedTicket);
        
        return updatedTicket;
    }

    @Transactional
    public Ticket updateTicketStatus(Long ticketId, Ticket.TicketStatus newStatus) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        ticket.setStatus(newStatus);
        
        // If resolved or closed, set resolvedAt timestamp
        if (newStatus == Ticket.TicketStatus.RESOLVED || newStatus == Ticket.TicketStatus.CLOSED) {
            ticket.setResolvedAt(LocalDateTime.now());
        } else {
            ticket.setResolvedAt(null);  // Clear resolution timestamp if reopened
        }
        
        Ticket updatedTicket = ticketRepository.save(ticket);
        
        // Notify about status change
        notificationService.notifyStatusChanged(updatedTicket);
        
        return updatedTicket;
    }

    @Transactional
    public Ticket updateTicketPriority(Long ticketId, Ticket.TicketPriority newPriority) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        ticket.setPriority(newPriority);
        return ticketRepository.save(ticket);
    }

    public Page<Ticket> getTicketsByUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return ticketRepository.findByCreatedBy(user, pageable);
    }

    public Page<Ticket> getAssignedTickets(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return ticketRepository.findByAssignedTo(user, pageable);
    }

    public Page<Ticket> getTicketsByDepartment(Integer departmentId, Pageable pageable) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        
        return ticketRepository.findByDepartment(department, pageable);
    }

    public Page<Ticket> getAllTickets(Pageable pageable) {
        return ticketRepository.findAll(pageable);
    }
    
    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));
    }
    
    public TicketAttachment getAttachmentById(Long id) {
        return ticketAttachmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attachment not found with id: " + id));
    }

    public List<TicketReply> getTicketReplies(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        return ticketReplyRepository.findByTicketOrderByCreatedAtAsc(ticket);
    }

    public List<TicketAttachment> getTicketAttachments(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        return ticketAttachmentRepository.findByTicket(ticket);
    }

    public List<Ticket> findTop10ByOrderByCreatedAtDesc() {
        return ticketRepository.findTop10ByOrderByCreatedAtDesc();
    }
    
    public long countByStatus(Ticket.TicketStatus status) {
        return ticketRepository.countByStatus(status);
    }
    
    public long countByAssignedTo(User user) {
        return ticketRepository.countByAssignedTo(user);
    }

    private TicketAttachment saveAttachment(MultipartFile file, Ticket ticket, User user) {
        try {
            // Create uploads directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate a unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            
            // Save the file to the filesystem
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath);
            
            // Create and save the attachment record
            TicketAttachment attachment = new TicketAttachment();
            attachment.setFileName(originalFilename);
            attachment.setFilePath(filePath.toString());
            attachment.setFileType(file.getContentType());
            attachment.setFileSize(file.getSize());
            attachment.setTicket(ticket);
            attachment.setUploadedBy(user);
            
            return ticketAttachmentRepository.save(attachment);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        return userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }
}
