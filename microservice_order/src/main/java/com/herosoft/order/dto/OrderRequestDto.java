package com.herosoft.order.dto;

import cn.hutool.core.date.DateTime;
import com.herosoft.commons.dto.message.ProductMessageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {
    @NotNull(message = "下单用户ID不能为空")
    private Integer userId;
    @NotNull(message = "下单总金额不能为空")
    private Double orderAmount;
    @NotNull(message = "订购产品列表不能为空")
    private List<ProductMessageDto> orderproducts;

    private DateTime createTime;
    private Integer createBy;
    private DateTime updateTime;
    private Integer updateBy;
}
