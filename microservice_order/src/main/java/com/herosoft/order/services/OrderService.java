package com.herosoft.order.services;

import com.herosoft.order.dto.OrderInfoDto;
import com.herosoft.order.dto.OrderRequestDto;

import java.io.IOException;

public interface OrderService {
    void downloadOrderByExcel(String fileName) throws IOException;

    OrderInfoDto createOrder(OrderRequestDto orderRequestDto);

    void generateOrderByExcel(String fileName);

    void cancelOrderWithoutStockChange(int userId, int orderHeaderId);

    void cancelOrderWithStockChange(int userId, int orderHeaderId) throws Exception;
}
