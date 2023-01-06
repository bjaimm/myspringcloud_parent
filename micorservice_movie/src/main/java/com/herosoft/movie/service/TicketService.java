package com.herosoft.movie.service;

import org.springframework.stereotype.Service;

@Service
public interface TicketService {
    int decreaseTicketNumberById(Integer id);

    String showTicket(Integer id);
}
