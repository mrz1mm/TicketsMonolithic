package com.ticketing.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ticketing.model.Ticket.TicketPriority;
import com.ticketing.model.Ticket.TicketStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object per i ticket.
 * Utilizzato per trasferire dati tra il livello di presentazione e il livello di servizio,
 * evitando l'esposizione diretta delle entità JPA.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketDto {
    
    private Long id;
    
    @NotBlank(message = "Il titolo è obbligatorio")
    @Size(min = 5, max = 100, message = "Il titolo deve essere compreso tra 5 e 100 caratteri")
    private String title;
    
    @NotBlank(message = "La descrizione è obbligatoria")
    @Size(min = 10, message = "La descrizione deve contenere almeno 10 caratteri")
    private String description;
    
    @NotNull(message = "La priorità è obbligatoria")
    private TicketPriority priority;
    
    private TicketStatus status;
    
    @NotNull(message = "Il dipartimento è obbligatorio")
    private Long departmentId;
    
    private String departmentName;
    
    private Long categoryId;
    
    private String categoryName;
    
    private Long assignedToId;
    
    private String assignedToName;
    
    private Long createdById;
    
    private String createdByName;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime resolvedAt;
}
