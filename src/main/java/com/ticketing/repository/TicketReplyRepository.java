package com.ticketing.repository;

import com.ticketing.model.Ticket;
import com.ticketing.model.TicketReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketReplyRepository extends JpaRepository<TicketReply, Long> {
    List<TicketReply> findByTicketOrderByCreatedAtAsc(Ticket ticket);
}
