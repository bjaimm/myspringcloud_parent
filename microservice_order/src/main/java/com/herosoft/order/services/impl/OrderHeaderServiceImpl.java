package com.herosoft.order.services.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.herosoft.order.dto.OrderInfoDto;
import com.herosoft.order.po.OrderHeaderPo;
import com.herosoft.order.mappers.OrderHeaderMapper;
import com.herosoft.order.services.OrderHeaderService;
import org.springframework.stereotype.Service;

@Service
public class OrderHeaderServiceImpl extends ServiceImpl<OrderHeaderMapper, OrderHeaderPo>
        implements OrderHeaderService {

    @Override
    public OrderInfoDto findOrderInfoByOrderHeaderId(int orderHeaderId) {
        return getBaseMapper().findOrderInfoByOrderHeaderId(orderHeaderId);
    }

    @Override
    public Page<OrderHeaderPo> listPage(Page<OrderHeaderPo> page, QueryWrapper<OrderHeaderPo> queryWrapper) {
        return getBaseMapper().selectPage(page,queryWrapper);
    }

    @Override
    public IPage<OrderInfoDto> listPageOrderInfo(Page<OrderInfoDto> page) {
        return getBaseMapper().selectPageOrderInfo(page);
    }
}
