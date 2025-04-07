package com.ticketing.repository;

import com.ticketing.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Trova un utente per username con tutti i suoi ruoli caricati in un'unica query
     */
    @EntityGraph(attributePaths = {"roles", "department"})
    Optional<User> findByUsername(String username);
    
    /**
     * Trova un utente per email con i suoi ruoli caricati
     */
    @EntityGraph(attributePaths = {"roles", "department"})
    Optional<User> findByEmail(String email);
    
    /**
     * Controlla se esiste un utente con il username specificato (per validazione)
     */
    boolean existsByUsername(String username);
    
    /**
     * Controlla se esiste un utente con l'email specificata (per validazione)
     */
    boolean existsByEmail(String email);
    
    /**
     * Trova tutti gli utenti con ruolo di supporto usando JOIN FETCH per ottimizzazione
     */
    @Query("SELECT DISTINCT u FROM User u " +
           "JOIN FETCH u.roles r " +
           "WHERE r.name = 'SUPPORT' " +
           "ORDER BY u.firstName, u.lastName")
    List<User> findAllSupportUsers();
    
    /**
     * Trova tutti gli utenti di un dipartimento specifico con i loro ruoli
     */
    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.roles " +
           "WHERE u.department.id = :departmentId")
    List<User> findByDepartmentId(@Param("departmentId") Long departmentId);
}
