package com.ticketing.service;

import com.ticketing.dto.TicketDto;
import com.ticketing.exception.ResourceNotFoundException;
import com.ticketing.model.Ticket;
import com.ticketing.model.User;
import com.ticketing.repository.TicketRepository;
import com.ticketing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servizio per la gestione dei ticket.
 * Fornisce metodi per creare, recuperare, aggiornare ed eliminare ticket,
 * nonché per gestire lo stato e le assegnazioni dei ticket.
 */
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final DepartmentService departmentService;
    private final CategoryService categoryService;

    /**
     * Recupera tutti i ticket paginati.
     *
     * @param pageable Oggetto pageable per gestire la paginazione
     * @return Page contenente i DTO dei ticket
     */
    public Page<TicketDto> getAllTickets(Pageable pageable) {
        return ticketRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    /**
     * Recupera un ticket specifico tramite il suo ID.
     *
     * @param id ID del ticket da recuperare
     * @return DTO del ticket richiesto
     * @throws ResourceNotFoundException se il ticket non esiste
     */
    public TicketDto getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", id));
        return convertToDto(ticket);
    }

    /**
     * Crea un nuovo ticket.
     *
     * @param ticketDto DTO contenente i dati del nuovo ticket
     * @param username Nome utente del creatore del ticket
     * @return Il ticket creato convertito in DTO
     * @throws ResourceNotFoundException se l'utente o altre entità correlate non esistono
     */
    @Transactional
    public TicketDto createTicket(TicketDto ticketDto, String username) {
        // Recupera l'utente che sta creando il ticket
        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Crea una nuova entità Ticket
        Ticket ticket = new Ticket();
        
        // Imposta i dati di base
        ticket.setTitle(ticketDto.getTitle());
        ticket.setDescription(ticketDto.getDescription());
        ticket.setPriority(ticketDto.getPriority());
        ticket.setStatus(Ticket.TicketStatus.OPEN); // Nuovo ticket sempre in stato OPEN
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setCreatedBy(creator);
        
        // Imposta relazioni con altre entità
        if (ticketDto.getDepartmentId() != null) {
            ticket.setDepartment(departmentService.getDepartmentEntityById(ticketDto.getDepartmentId()));
        }
        
        if (ticketDto.getCategoryId() != null) {
            ticket.setCategory(categoryService.getCategoryEntityById(ticketDto.getCategoryId()));
        }
        
        if (ticketDto.getAssignedToId() != null) {
            User assignee = userRepository.findById(ticketDto.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", ticketDto.getAssignedToId()));
            ticket.setAssignedTo(assignee);
        }
        
        // Salva il ticket nel database
        Ticket savedTicket = ticketRepository.save(ticket);
        
        return convertToDto(savedTicket);
    }

    /**
     * Aggiorna un ticket esistente.
     *
     * @param id ID del ticket da aggiornare
     * @param ticketDto DTO contenente i nuovi dati del ticket
     * @return Il ticket aggiornato convertito in DTO
     * @throws ResourceNotFoundException se il ticket o altre entità correlate non esistono
     */
    @Transactional
    public TicketDto updateTicket(Long id, TicketDto ticketDto) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", id));
        
        // Aggiorna i campi di base
        ticket.setTitle(ticketDto.getTitle());
        ticket.setDescription(ticketDto.getDescription());
        ticket.setPriority(ticketDto.getPriority());
        
        // Se lo stato è cambiato, registra anche la data di risoluzione se necessario
        if (ticketDto.getStatus() != null && ticket.getStatus() != ticketDto.getStatus()) {
            ticket.setStatus(ticketDto.getStatus());
            
            // Se il ticket è stato risolto, imposta la data di risoluzione
            if (ticketDto.getStatus() == Ticket.TicketStatus.RESOLVED) {
                ticket.setResolvedAt(LocalDateTime.now());
            } else {
                ticket.setResolvedAt(null); // Rimuovi la data di risoluzione se lo stato è cambiato da RESOLVED
            }
        }
        
        // Aggiorna le relazioni
        if (ticketDto.getDepartmentId() != null) {
            ticket.setDepartment(departmentService.getDepartmentEntityById(ticketDto.getDepartmentId()));
        }
        
        if (ticketDto.getCategoryId() != null) {
            ticket.setCategory(categoryService.getCategoryEntityById(ticketDto.getCategoryId()));
        } else {
            ticket.setCategory(null);
        }
        
        // Aggiorna l'assegnatario del ticket
        if (ticketDto.getAssignedToId() != null) {
            User assignee = userRepository.findById(ticketDto.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", ticketDto.getAssignedToId()));
            ticket.setAssignedTo(assignee);
        } else {
            ticket.setAssignedTo(null);
        }
        
        ticket.setUpdatedAt(LocalDateTime.now());
        
        Ticket updatedTicket = ticketRepository.save(ticket);
        
        return convertToDto(updatedTicket);
    }

    /**
     * Recupera i ticket assegnati a un utente specifico.
     *
     * @param userId ID dell'utente assegnatario
     * @return Lista di DTO dei ticket assegnati all'utente
     */
    public List<TicketDto> getTicketsByAssignee(Long userId) {
        return ticketRepository.findByAssignedToId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Recupera i ticket creati da un utente specifico.
     *
     * @param userId ID dell'utente creatore
     * @return Lista di DTO dei ticket creati dall'utente
     */
    public List<TicketDto> getTicketsByCreator(Long userId) {
        return ticketRepository.findByCreatedById(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Recupera i ticket filtrati per stato.
     *
     * @param status Stato per cui filtrare i ticket
     * @return Lista di DTO dei ticket con lo stato specificato
     */
    public List<TicketDto> getTicketsByStatus(Ticket.TicketStatus status) {
        return ticketRepository.findByStatus(status).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Converte un'entità Ticket in un DTO.
     *
     * @param ticket Entità Ticket da convertire
     * @return TicketDto corrispondente
     */
    private TicketDto convertToDto(Ticket ticket) {
        TicketDto dto = new TicketDto();
        dto.setId(ticket.getId());
        dto.setTitle(ticket.getTitle());
        dto.setDescription(ticket.getDescription());
        dto.setPriority(ticket.getPriority());
        dto.setStatus(ticket.getStatus());
        
        if (ticket.getDepartment() != null) {
            dto.setDepartmentId(ticket.getDepartment().getId());
            dto.setDepartmentName(ticket.getDepartment().getName());
        }
        
        if (ticket.getCategory() != null) {
            dto.setCategoryId(ticket.getCategory().getId());
            dto.setCategoryName(ticket.getCategory().getName());
        }
        
        if (ticket.getAssignedTo() != null) {
            dto.setAssignedToId(ticket.getAssignedTo().getId());
            dto.setAssignedToName(ticket.getAssignedTo().getFirstName() + " " + ticket.getAssignedTo().getLastName());
        }
        
        if (ticket.getCreatedBy() != null) {
            dto.setCreatedById(ticket.getCreatedBy().getId());
            dto.setCreatedByName(ticket.getCreatedBy().getFirstName() + " " + ticket.getCreatedBy().getLastName());
        }
        
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setUpdatedAt(ticket.getUpdatedAt());
        dto.setResolvedAt(ticket.getResolvedAt());
        
        return dto;
    }
}
