package com.herosoft.product.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.herosoft.product.po.ProductChangeLogPo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductChangeLogMapper extends BaseMapper<ProductChangeLogPo> {
}
