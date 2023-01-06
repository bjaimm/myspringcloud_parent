package com.herosoft.movie.service.impl;

import com.herosoft.movie.dao.TicketMapper;
import com.herosoft.movie.model.Ticket;
import com.herosoft.movie.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TicketServiceImpl implements TicketService {

    @Autowired
    TicketMapper ticketMapper;

    @Override
    public int decreaseTicketNumberById(Integer id) {
        Ticket ticket = ticketMapper.load(id);

        ticket.setNumber(ticket.getNumber()-1);

        return ticketMapper.update(ticket);
    }

    @Override
    public String showTicket(Integer id) {
        Ticket ticket = ticketMapper.load(id);

        return "当前电影"+ticket.getMoviename()+"剩余"+ticket.getNumber()+"张票";
    }
}
