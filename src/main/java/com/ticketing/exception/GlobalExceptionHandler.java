package com.ticketing.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFoundException(ResourceNotFoundException ex, Model model, HttpServletRequest request) {
        logger.error("Risorsa non trovata: {}", ex.getMessage());
        
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("error", "Non trovato");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("path", request.getRequestURI());
        
        return "error/404";
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDeniedException(AccessDeniedException ex, Model model, HttpServletRequest request) {
        logger.error("Accesso negato: {}", ex.getMessage());
        
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("status", HttpStatus.FORBIDDEN.value());
        model.addAttribute("error", "Accesso negato");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("path", request.getRequestURI());
        
        return "error/403";
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequestException(BadRequestException ex, Model model, HttpServletRequest request) {
        logger.error("Richiesta non valida: {}", ex.getMessage());
        
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("status", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("error", "Richiesta non valida");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("path", request.getRequestURI());
        
        return "error/400";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model, HttpServletRequest request) {
        logger.error("Errore interno del server", ex);
        
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("error", "Errore interno del server");
        model.addAttribute("message", "Si è verificato un errore imprevisto. Gli amministratori sono stati avvisati.");
        model.addAttribute("path", request.getRequestURI());
        
        return "error/500";
    }
}
