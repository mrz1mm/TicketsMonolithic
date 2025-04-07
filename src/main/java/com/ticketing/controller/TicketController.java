package com.ticketing.controller;

import com.ticketing.dto.TicketDto;
import com.ticketing.exception.ResourceNotFoundException;
import com.ticketing.model.Ticket.TicketPriority;
import com.ticketing.model.Ticket.TicketStatus;
import com.ticketing.service.CategoryService;
import com.ticketing.service.DepartmentService;
import com.ticketing.service.TicketService;
import com.ticketing.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller per la gestione delle operazioni sui ticket.
 * Gestisce le richieste relative alla visualizzazione, creazione, modifica ed eliminazione dei ticket.
 */
@Controller
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {
    
    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);
    private static final int PAGE_SIZE = 10;
    
    private final TicketService ticketService;
    private final UserService userService;
    private final DepartmentService departmentService;
    private final CategoryService categoryService;

    /**
     * Visualizza l'elenco dei ticket con paginazione.
     *
     * @param page Numero di pagina (zero-based)
     * @param model Model per l'aggiunta di attributi alla vista
     * @return Nome della vista Thymeleaf
     */
    @GetMapping
    public String listTickets(@RequestParam(defaultValue = "0") int page, Model model) {
        logger.debug("Richiesta di elenco ticket, pagina: {}", page);
        
        // Crea un oggetto Pageable per la paginazione, ordinando per data di creazione discendente
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());
        
        // Ottieni i ticket paginati dal service
        Page<TicketDto> ticketPage = ticketService.getAllTickets(pageable);
        
        // Aggiungi i dati necessari al model per la visualizzazione
        model.addAttribute("ticketPage", ticketPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ticketPage.getTotalPages());
        model.addAttribute("totalTickets", ticketPage.getTotalElements());
        
        // Aggiungi dati necessari per i filtri nella UI
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("statuses", TicketStatus.values());
        
        return "tickets/list";
    }

    /**
     * Visualizza i dettagli di un ticket specifico.
     *
     * @param id ID del ticket da visualizzare
     * @param model Model per l'aggiunta di attributi alla vista
     * @return Nome della vista Thymeleaf
     */
    @GetMapping("/{id}")
    public String viewTicket(@PathVariable Long id, Model model) {
        try {
            logger.debug("Richiesta di visualizzazione ticket con ID: {}", id);
            TicketDto ticket = ticketService.getTicketById(id);
            model.addAttribute("ticket", ticket);
            return "tickets/view";
        } catch (ResourceNotFoundException e) {
            logger.error("Ticket non trovato con ID: {}", id, e);
            // Gestione dell'errore tramite GlobalExceptionHandler
            throw e;
        }
    }

    /**
     * Mostra il form per la creazione di un nuovo ticket.
     *
     * @param model Model per l'aggiunta di attributi alla vista
     * @return Nome della vista Thymeleaf
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        logger.debug("Mostra form per creazione nuovo ticket");
        
        // Prepara un nuovo DTO vuoto per il form
        model.addAttribute("ticket", new TicketDto());
        
        // Aggiunge dati di supporto per il form
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("supportUsers", userService.getSupportUsers());
        model.addAttribute("priorities", TicketPriority.values());
        
        return "tickets/create";
    }

    /**
     * Elabora la richiesta di creazione di un nuovo ticket.
     *
     * @param ticketDto DTO contenente i dati del nuovo ticket
     * @param result Risultato della validazione
     * @param authentication Oggetto authentication per ottenere l'utente corrente
     * @param redirectAttributes Attributi per i messaggi di redirect
     * @param model Model per l'aggiunta di attributi alla vista in caso di errori
     * @return Redirect alla pagina del ticket creato o ritorno al form in caso di errori
     */
    @PostMapping("/create")
    public String createTicket(
            @Valid @ModelAttribute("ticket") TicketDto ticketDto,
            BindingResult result,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        logger.debug("Richiesta di creazione nuovo ticket: {}", ticketDto.getTitle());
        
        // Se ci sono errori di validazione, ritorna al form
        if (result.hasErrors()) {
            logger.debug("Errori di validazione nella creazione del ticket: {}", result.getAllErrors());
            
            // Riaggiunge i dati di supporto al modello
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("supportUsers", userService.getSupportUsers());
            model.addAttribute("priorities", TicketPriority.values());
            
            return "tickets/create";
        }
        
        try {
            // Utilizza il nome utente dell'utente autenticato per creare il ticket
            TicketDto createdTicket = ticketService.createTicket(ticketDto, authentication.getName());
            
            // Aggiunge messaggio di successo per la pagina di destinazione
            redirectAttributes.addFlashAttribute("successMessage", "Ticket creato con successo");
            
            // Redirect alla pagina del ticket appena creato
            return "redirect:/tickets/" + createdTicket.getId();
        } catch (Exception e) {
            // Gestione degli errori durante la creazione
            logger.error("Errore durante la creazione del ticket", e);
            
            model.addAttribute("errorMessage", "Si è verificato un errore: " + e.getMessage());
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("supportUsers", userService.getSupportUsers());
            model.addAttribute("priorities", TicketPriority.values());
            
            return "tickets/create";
        }
    }

    /**
     * Mostra il form per la modifica di un ticket esistente.
     *
     * @param id ID del ticket da modificare
     * @param model Model per l'aggiunta di attributi alla vista
     * @return Nome della vista Thymeleaf
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            logger.debug("Mostra form per modifica ticket con ID: {}", id);
            
            TicketDto ticket = ticketService.getTicketById(id);
            
            model.addAttribute("ticket", ticket);
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("supportUsers", userService.getSupportUsers());
            model.addAttribute("statuses", TicketStatus.values());
            model.addAttribute("priorities", TicketPriority.values());
            
            return "tickets/edit";
        } catch (ResourceNotFoundException e) {
            logger.error("Ticket non trovato con ID: {}", id, e);
            // Gestione dell'errore tramite GlobalExceptionHandler
            throw e;
        }
    }

    /**
     * Elabora la richiesta di aggiornamento di un ticket esistente.
     *
     * @param id ID del ticket da aggiornare
     * @param ticketDto DTO contenente i dati aggiornati del ticket
     * @param result Risultato della validazione
     * @param redirectAttributes Attributi per i messaggi di redirect
     * @param model Model per l'aggiunta di attributi alla vista in caso di errori
     * @return Redirect alla pagina del ticket aggiornato o ritorno al form in caso di errori
     */
    @PostMapping("/{id}/update")
    public String updateTicket(
            @PathVariable Long id,
            @Valid @ModelAttribute("ticket") TicketDto ticketDto,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        logger.debug("Richiesta di aggiornamento ticket con ID: {}", id);
        
        if (result.hasErrors()) {
            logger.debug("Errori di validazione nell'aggiornamento del ticket: {}", result.getAllErrors());
            
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("supportUsers", userService.getSupportUsers());
            model.addAttribute("statuses", TicketStatus.values());
            model.addAttribute("priorities", TicketPriority.values());
            
            return "tickets/edit";
        }
        
        try {
            // Aggiorna il ticket attraverso il servizio
            ticketService.updateTicket(id, ticketDto);
            
            // Aggiunge messaggio di successo
            redirectAttributes.addFlashAttribute("successMessage", "Ticket aggiornato con successo");
            
            return "redirect:/tickets/" + id;
        } catch (Exception e) {
            logger.error("Errore durante l'aggiornamento del ticket", e);
            
            model.addAttribute("errorMessage", "Si è verificato un errore: " + e.getMessage());
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("supportUsers", userService.getSupportUsers());
            model.addAttribute("statuses", TicketStatus.values());
            model.addAttribute("priorities", TicketPriority.values());
            
            return "tickets/edit";
        }
    }
}
