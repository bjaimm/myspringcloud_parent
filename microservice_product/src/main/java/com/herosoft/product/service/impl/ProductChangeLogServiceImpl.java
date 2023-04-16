package com.herosoft.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.herosoft.product.mappers.ProductChangeLogMapper;
import com.herosoft.product.po.ProductChangeLogPo;
import com.herosoft.product.service.ProductChangeLogService;
import org.springframework.stereotype.Service;

@Service
public class ProductChangeLogServiceImpl extends ServiceImpl<ProductChangeLogMapper, ProductChangeLogPo>
implements ProductChangeLogService {
}
