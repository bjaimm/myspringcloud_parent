package com.herosoft.product.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.herosoft.commons.dto.ProductDto;
import com.herosoft.product.po.ProductPo;
import com.herosoft.product.service.impl.ProductServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/products")
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
}
