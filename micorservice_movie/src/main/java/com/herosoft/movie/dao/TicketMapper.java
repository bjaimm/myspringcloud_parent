package com.herosoft.movie.dao;

import com.herosoft.movie.po.TicketPo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TicketMapper {
    void add(TicketPo ticketPo);

    int update(TicketPo ticketPo);

    void delete(Integer id);

    TicketPo load(Integer id);
}
