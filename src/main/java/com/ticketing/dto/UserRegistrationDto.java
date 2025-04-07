package com.ticketing.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRegistrationDto {
    
    @NotBlank(message = "Il nome utente è obbligatorio")
    @Size(min = 3, max = 50, message = "Il nome utente deve essere compreso tra 3 e 50 caratteri")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Il nome utente può contenere solo lettere, numeri, punti, trattini e underscore")
    private String username;
    
    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "Inserisci un indirizzo email valido")
    private String email;
    
    @NotBlank(message = "La password è obbligatoria")
    @Size(min = 6, message = "La password deve contenere almeno 6 caratteri")
    private String password;
    
    @NotBlank(message = "La conferma password è obbligatoria")
    private String confirmPassword;
    
    @NotBlank(message = "Il nome è obbligatorio")
    private String firstName;
    
    @NotBlank(message = "Il cognome è obbligatorio")
    private String lastName;
    
    private Long departmentId;
    
    // Getter e setter generati da Lombok
}
