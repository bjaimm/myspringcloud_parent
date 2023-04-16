package com.herosoft.product.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.herosoft.commons.dto.OrderDetailDto;
import com.herosoft.commons.dto.ProductDto;
import com.herosoft.commons.results.Result;
import com.herosoft.product.po.ProductPo;
import com.herosoft.product.service.impl.ProductServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/products")
@Slf4j
public class ProductController {
    @Autowired
    private ProductServiceImpl productService;

    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public ProductPo findById(@PathVariable Integer id){
        QueryWrapper<ProductPo> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("productid",id);
        return productService.getOne(queryWrapper);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ProductDto> findAll(){
        List<ProductDto> productDtoList = productService.list().stream().map(productPo -> {
            ProductDto productDto = new ProductDto();
            BeanUtils.copyProperties(productPo,productDto);

            return productDto;
        }).collect(Collectors.toList());

        return productDtoList;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String add(@RequestBody @Valid ProductPo product){
        productService.save(product);
        return "添加成功";
    }

    @DeleteMapping(value = "/{productId}")
    public String delete(@PathVariable Integer productId){
        productService.removeById(productId);
        return "删除成功";
    }

    @PutMapping
    public String update(@RequestBody @Valid ProductPo product) {
        productService.updateById(product);
        return "修改成功";
    }

    @RequestMapping(method = RequestMethod.PUT,value="/deduceStock",produces = "application/json")
    @Transactional
    public String deduceStock(@RequestBody List<OrderDetailDto> orderDetailDto){
        log.info("收到还原库存请求。。。");
        List<ProductPo> productPoList = orderDetailDto.stream()
                .map(orderDetail -> {
                    ProductPo productPo = productService.getById(orderDetail.getProductId());

                    productPo.setProductAmount(productPo.getProductAmount() + orderDetail.getProductQty());

                    return productPo;
                })
                .collect(Collectors.toList());

        productService.updateBatchById(productPoList);

        return "库存回滚成功";
    };
}
