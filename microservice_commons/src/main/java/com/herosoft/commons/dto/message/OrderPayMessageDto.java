package com.herosoft.commons.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPayMessageDto {
    private Integer orderHeaderId;
    private Integer userId;
    private Double orderAmount;
}
