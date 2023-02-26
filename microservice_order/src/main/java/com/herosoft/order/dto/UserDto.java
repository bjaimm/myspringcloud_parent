package com.herosoft.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Integer userId;
    private String userName;
    private Double balance;
    private LocalDateTime createdt;
    private LocalDateTime updatedt;
    private String sex;
}
