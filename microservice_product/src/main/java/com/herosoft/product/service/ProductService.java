package com.herosoft.product.service;

import com.herosoft.product.dao.ProductDao;
import com.herosoft.product.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductDao productDao;

    public Product findById(Integer id){
        return productDao.load(id);
    }

    public void add(Product product){
        productDao.add(product);
    }
}
