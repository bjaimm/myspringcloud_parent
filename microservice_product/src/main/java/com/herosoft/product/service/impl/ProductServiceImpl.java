package com.herosoft.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.herosoft.commons.dto.message.OrderCreateMessageDto;
import com.herosoft.product.mappers.ProductMapper;
import com.herosoft.product.po.ProductPo;
import com.herosoft.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, ProductPo>
                        implements ProductService
{
    @Autowired
    private ProductMapper productMapper;

    @Override
    public int reduceProductQty(int productId, int productQty) {
        ProductPo productPo = Optional.ofNullable(productMapper.selectById(productId)).orElse(new ProductPo());
        if (productPo.getProductId()<=0 || productPo.getProductId()==null){
            return 0;
        }

        Integer currentProductAmount = productPo.getProductAmount();

        ProductPo productPoUpdate = new ProductPo();
        productPoUpdate.setProductAmount(currentProductAmount-productQty);

        UpdateWrapper<ProductPo> productUpdateWrapper = new UpdateWrapper<>();
        productUpdateWrapper.eq("productid",productId)
                .ge("productamount",productQty);
        int updateResult =productMapper.update(productPoUpdate,productUpdateWrapper);

        return updateResult;
    }

    @Override
    @Transactional
    public boolean reduceProductQtyByMessage(OrderCreateMessageDto orderCreateMessageDto) {
        AtomicBoolean reduceSuccess = new AtomicBoolean(true);

        orderCreateMessageDto.getProductList().forEach(productDto -> {
            if(reduceProductQty(productDto.getProductId(),productDto.getProductQty())==0){
                reduceSuccess.set(false);
            }
        });
        if(!reduceSuccess.get()){
            throw new RuntimeException("扣减库存未全部成功,订单ID:"+
                    orderCreateMessageDto.getOrderHeaderId()+
                    "产品库存未更新");
        }
        return reduceSuccess.get();
    }
}
