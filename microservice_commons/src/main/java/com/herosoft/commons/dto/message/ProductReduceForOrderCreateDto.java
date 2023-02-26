package com.herosoft.commons.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductReduceForOrderCreateDto {
    private int orderHeaderId;
    private boolean reduceProductSuccess;
}
