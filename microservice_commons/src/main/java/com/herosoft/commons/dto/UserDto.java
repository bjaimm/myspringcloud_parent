package com.herosoft.commons.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto implements Serializable {
    private Integer userId;
    private String userName;
    private String password;
    private String userType;
    private Double balance;
    private String sex;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;
}
