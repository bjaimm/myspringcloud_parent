package com.herosoft.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDto {

    private int orderDetailId;
    private int productId;
    private String productName;
    private Double productPrice;
    private int productQty;
}
