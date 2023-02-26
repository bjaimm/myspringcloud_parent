package com.herosoft.product.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.herosoft.product.po.ProductPo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<ProductPo> {


}
