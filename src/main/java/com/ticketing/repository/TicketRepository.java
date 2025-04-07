package com.ticketing.repository;

import com.ticketing.model.Department;
import com.ticketing.model.Ticket;
import com.ticketing.model.User;
import com.ticketing.model.Ticket.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @EntityGraph(attributePaths = {"createdBy", "assignedTo", "department", "category"})
    Optional<Ticket> findById(Long id);

    @EntityGraph(attributePaths = {"createdBy", "assignedTo", "department", "category"})
    Page<Ticket> findAll(Pageable pageable);

    @Query("SELECT t FROM Ticket t " +
           "LEFT JOIN FETCH t.createdBy " +
           "LEFT JOIN FETCH t.assignedTo " +
           "LEFT JOIN FETCH t.department " +
           "LEFT JOIN FETCH t.category " +
           "WHERE t.status = :status")
    List<Ticket> findByStatus(@Param("status") TicketStatus status);

    @Query("SELECT t FROM Ticket t " +
           "LEFT JOIN FETCH t.createdBy " +
           "LEFT JOIN FETCH t.department " +
           "LEFT JOIN FETCH t.category " +
           "WHERE t.assignedTo.id = :userId")
    List<Ticket> findByAssignedToId(@Param("userId") Long userId);

    @Query("SELECT t FROM Ticket t " +
           "LEFT JOIN FETCH t.assignedTo " +
           "LEFT JOIN FETCH t.department " +
           "LEFT JOIN FETCH t.category " +
           "WHERE t.createdBy.id = :userId")
    List<Ticket> findByCreatedById(@Param("userId") Long userId);

    @Query(value = "SELECT t FROM Ticket t " +
                   "LEFT JOIN FETCH t.createdBy " +
                   "LEFT JOIN FETCH t.assignedTo " +
                   "LEFT JOIN FETCH t.department " +
                   "LEFT JOIN FETCH t.category " +
                   "WHERE t.createdAt BETWEEN :startDate AND :endDate",
           countQuery = "SELECT COUNT(t) FROM Ticket t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    Page<Ticket> findByCreatedAtBetween(
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate,
            Pageable pageable);

    @Query(value = "SELECT DISTINCT t FROM Ticket t " +
                  "LEFT JOIN FETCH t.createdBy " +
                  "LEFT JOIN FETCH t.assignedTo " +
                  "LEFT JOIN FETCH t.department " +
                  "LEFT JOIN FETCH t.category " +
                  "WHERE (:status IS NULL OR t.status = :status) " +
                  "AND (:priority IS NULL OR t.priority = :priority) " +
                  "AND (:departmentId IS NULL OR t.department.id = :departmentId) " +
                  "AND (:assigneeId IS NULL OR t.assignedTo.id = :assigneeId)",
            countQuery = "SELECT COUNT(DISTINCT t) FROM Ticket t " +
                         "WHERE (:status IS NULL OR t.status = :status) " +
                         "AND (:priority IS NULL OR t.priority = :priority) " +
                         "AND (:departmentId IS NULL OR t.department.id = :departmentId) " +
                         "AND (:assigneeId IS NULL OR t.assignedTo.id = :assigneeId)")
    Page<Ticket> searchTickets(
            @Param("status") TicketStatus status,
            @Param("priority") Ticket.TicketPriority priority,
            @Param("departmentId") Long departmentId,
            @Param("assigneeId") Long assigneeId,
            Pageable pageable);

    Page<Ticket> findByCreatedBy(User user, Pageable pageable);
    Page<Ticket> findByAssignedTo(User user, Pageable pageable);
    Page<Ticket> findByDepartment(Department department, Pageable pageable);
    Page<Ticket> findByPriority(Ticket.TicketPriority priority, Pageable pageable);

    List<Ticket> findTop10ByOrderByCreatedAtDesc();

    long countByStatus(Ticket.TicketStatus status);
    long countByAssignedTo(User assignedTo);
}
