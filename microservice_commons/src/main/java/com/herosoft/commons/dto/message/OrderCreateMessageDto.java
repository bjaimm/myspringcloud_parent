package com.herosoft.commons.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateMessageDto {
    private Integer orderHeaderId;
    private int orderDetailId;
    private Integer userId;
    private Double orderAmount;
    private List<ProductMessageDto> productList;
}
