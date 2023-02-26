package com.herosoft.commons.dto;

import lombok.Data;

@Data
public class ProductDto {
    private int productId;
    private String productName;
    private int productAmount;
    private double productPrice;
}
