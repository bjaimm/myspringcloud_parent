package com.herosoft.movie.dao;

import com.herosoft.movie.model.Ticket;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TicketMapper {
    void add(Ticket ticket);

    int update(Ticket ticket);

    void delete(Integer id);

    Ticket load(Integer id);
}
