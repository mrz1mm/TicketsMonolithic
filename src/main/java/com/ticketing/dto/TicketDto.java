package com.ticketing.dto;

import com.ticketing.model.Ticket;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketDto {

    private Long id;
    
    @NotEmpty(message = "Title is required")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    private String title;
    
    @NotEmpty(message = "Description is required")
    private String description;
    
    private Ticket.TicketStatus status;
    
    private Ticket.TicketPriority priority;
    
    @NotNull(message = "Department is required")
    private Integer departmentId;
    
    private Long assignedToId;
    
    private Set<Integer> categoryIds = new HashSet<>();
    
    private List<MultipartFile> attachments = new ArrayList<>();
    
    // Additional fields for displaying ticket details
    private String createdByUsername;
    private String assignedToUsername;
    private String departmentName;
    private Set<String> categoryNames;
    private LocalDateTimeDto createdAt;
    private LocalDateTimeDto updatedAt;
    private LocalDateTimeDto resolvedAt;
}
