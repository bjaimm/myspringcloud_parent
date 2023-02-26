package com.herosoft.movie.service.impl;

import com.herosoft.movie.dao.TicketMapper;
import com.herosoft.movie.po.TicketPo;
import com.herosoft.movie.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TicketServiceImpl implements TicketService {

    @Autowired
    TicketMapper ticketMapper;

    @Override
    public int decreaseTicketNumberById(Integer id) {
        TicketPo ticketPo = ticketMapper.load(id);

        ticketPo.setNumber(ticketPo.getNumber()-1);

        return ticketMapper.update(ticketPo);
    }

    @Override
    public String showTicket(Integer id) {
        TicketPo ticketPo = ticketMapper.load(id);

        return "当前电影"+ ticketPo.getMoviename()+"剩余"+ ticketPo.getNumber()+"张票";
    }
}
