package com.ticketing.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketReplyDto {

    private Long id;
    
    @NotEmpty(message = "Content is required")
    private String content;
    
    @NotNull(message = "Ticket ID is required")
    private Long ticketId;
    
    private boolean internalNote;
    
    private List<MultipartFile> attachments = new ArrayList<>();
    
    // Additional fields for displaying reply details
    private String userFullName;
    private String userUsername;
    private LocalDateTimeDto createdAt;
}
