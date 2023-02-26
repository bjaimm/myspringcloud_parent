package com.herosoft.order.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.herosoft.order.enums.OrderStatus;
import com.herosoft.order.po.OrderHeaderPo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfoDto {
    @ExcelProperty("订单流水号")
    private int orderHeaderId;
    @ExcelProperty("订单金额")
    private double orderAmount;

    private int orderStatus;
    @ExcelProperty("订单状态")
    private String orderStatusMessage;
    private List<OrderDetailDto> orderDetailList;

    public OrderInfoDto(OrderHeaderPo orderHeaderPo) {
        this.orderHeaderId=orderHeaderPo.getOrderHeaderId();
        this.orderStatus=orderHeaderPo.getStatus();
        this.orderAmount=orderHeaderPo.getOrderAmount();
        this.orderStatusMessage = OrderStatus.values()[orderHeaderPo.getStatus()].getOrderStatusMessage();
    }
}
