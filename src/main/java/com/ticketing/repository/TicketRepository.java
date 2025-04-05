package com.ticketing.repository;

import com.ticketing.model.Department;
import com.ticketing.model.Ticket;
import com.ticketing.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Page<Ticket> findByCreatedBy(User user, Pageable pageable);
    Page<Ticket> findByAssignedTo(User user, Pageable pageable);
    Page<Ticket> findByDepartment(Department department, Pageable pageable);
    Page<Ticket> findByStatus(Ticket.TicketStatus status, Pageable pageable);
    Page<Ticket> findByPriority(Ticket.TicketPriority priority, Pageable pageable);
    
    List<Ticket> findTop10ByOrderByCreatedAtDesc();
    
    long countByStatus(Ticket.TicketStatus status);
    long countByAssignedTo(User assignedTo);
}
