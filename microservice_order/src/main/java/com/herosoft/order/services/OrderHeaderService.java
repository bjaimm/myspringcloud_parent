package com.herosoft.order.services;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.herosoft.order.dto.OrderInfoDto;
import com.herosoft.order.po.OrderHeaderPo;

public interface OrderHeaderService extends IService<OrderHeaderPo> {
    OrderInfoDto findOrderInfoByOrderHeaderId(int orderHeaderId);

    Page<OrderHeaderPo> listPage(Page<OrderHeaderPo> page, QueryWrapper<OrderHeaderPo> queryWrapper);

    IPage<OrderInfoDto> listPageOrderInfo(Page<OrderInfoDto> page);
}
