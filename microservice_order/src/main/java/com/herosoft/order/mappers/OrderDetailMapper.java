package com.herosoft.order.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.herosoft.order.po.OrderDetailPo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetailPo> {
}