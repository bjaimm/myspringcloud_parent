package com.herosoft.order.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {

    OPEN(0,"已建单"),
    STOCKOUT(1,"库存已扣减"),
    PAID(2,"已支付"),
    CANCEL(3,"已取消");

    private int orderStatus;
    private String orderStatusMessage;

    OrderStatus(int orderStatus,String orderStatusMessage) {
        this.orderStatus = orderStatus;
        this.orderStatusMessage = orderStatusMessage;
    }
}
