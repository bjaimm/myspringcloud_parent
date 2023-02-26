package com.herosoft.order.services.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.herosoft.order.po.OrderDetailPo;
import com.herosoft.order.mappers.OrderDetailMapper;
import com.herosoft.order.services.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetailPo>
        implements OrderDetailService {
}
