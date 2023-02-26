package com.herosoft.order.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.herosoft.order.dto.OrderInfoDto;
import com.herosoft.order.po.OrderHeaderPo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderHeaderMapper extends BaseMapper<OrderHeaderPo> {
    OrderInfoDto findOrderInfoByOrderHeaderId(int orderHeaderId);

    IPage<OrderInfoDto> selectPageOrderInfo(Page<OrderInfoDto> page);
}