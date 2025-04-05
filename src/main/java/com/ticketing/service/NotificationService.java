package com.ticketing.service;

import com.ticketing.model.Ticket;
import com.ticketing.model.TicketReply;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Async
    public void notifyNewTicket(Ticket ticket) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "NEW_TICKET");
        notification.put("ticketId", ticket.getId());
        notification.put("title", ticket.getTitle());
        notification.put("createdBy", ticket.getCreatedBy().getUsername());
        notification.put("departmentId", ticket.getDepartment().getId());
        
        // Broadcast to department topic
        messagingTemplate.convertAndSend(
            "/topic/department/" + ticket.getDepartment().getId(),
            notification
        );
        
        // Also send to admin channel
        messagingTemplate.convertAndSend("/topic/admin", notification);
    }

    @Async
    public void notifyNewReply(TicketReply reply) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "NEW_REPLY");
        notification.put("ticketId", reply.getTicket().getId());
        notification.put("replyId", reply.getId());
        notification.put("content", reply.getContent().substring(0, Math.min(100, reply.getContent().length())));
        notification.put("username", reply.getUser().getUsername());
        
        // Notify ticket creator
        messagingTemplate.convertAndSendToUser(
            reply.getTicket().getCreatedBy().getUsername(),
            "/queue/notifications",
            notification
        );
        
        // If ticket is assigned, notify assignee
        if (reply.getTicket().getAssignedTo() != null) {
            messagingTemplate.convertAndSendToUser(
                reply.getTicket().getAssignedTo().getUsername(),
                "/queue/notifications",
                notification
            );
        }
    }

    @Async
    public void notifyTicketAssigned(Ticket ticket) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "TICKET_ASSIGNED");
        notification.put("ticketId", ticket.getId());
        notification.put("title", ticket.getTitle());
        notification.put("assignee", ticket.getAssignedTo().getUsername());
        
        // Notify the assignee
        messagingTemplate.convertAndSendToUser(
            ticket.getAssignedTo().getUsername(),
            "/queue/notifications",
            notification
        );
    }

    @Async
    public void notifyStatusChanged(Ticket ticket) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "STATUS_CHANGED");
        notification.put("ticketId", ticket.getId());
        notification.put("title", ticket.getTitle());
        notification.put("status", ticket.getStatus());
        
        // Notify ticket creator
        messagingTemplate.convertAndSendToUser(
            ticket.getCreatedBy().getUsername(),
            "/queue/notifications",
            notification
        );
        
        // If ticket is assigned, notify assignee
        if (ticket.getAssignedTo() != null) {
            messagingTemplate.convertAndSendToUser(
                ticket.getAssignedTo().getUsername(),
                "/queue/notifications",
                notification
            );
        }
    }
}
