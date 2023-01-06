package com.herosoft.product.controller;

import com.herosoft.product.model.Product;
import com.herosoft.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public Product findById(@PathVariable Integer id){
        return productService.findById(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String add(@RequestBody Product product){
        productService.add(product);
        return "添加成功";
    }
}
