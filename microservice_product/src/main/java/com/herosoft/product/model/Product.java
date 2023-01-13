package com.herosoft.product.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Product {
    private Integer id;
    private String name;
    private Double price;
    private Double amount;
}
