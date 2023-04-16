package com.herosoft.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.herosoft.commons.dto.message.OrderCreateMessageDto;
import com.herosoft.product.po.ProductPo;

public interface ProductService extends IService<ProductPo> {
    int reduceProductQty(int productId, int productQty);

    boolean reduceProductQtyByMessage(OrderCreateMessageDto orderCreateMessageDto);
}
