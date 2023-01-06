package com.herosoft.movie.model;

import lombok.Data;
import org.apache.ibatis.annotations.Mapper;

@Data
public class Ticket {
    private Integer id;
    private String moviename;
    private Long number;
}
