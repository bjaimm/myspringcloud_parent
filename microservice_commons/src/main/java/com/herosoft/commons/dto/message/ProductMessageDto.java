package com.herosoft.commons.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductMessageDto {
    @NotNull
    private Integer productId;
    @NotNull
    private Integer productQty;
}
