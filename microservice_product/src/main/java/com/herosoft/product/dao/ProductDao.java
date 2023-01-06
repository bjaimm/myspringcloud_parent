package com.herosoft.product.dao;

import com.herosoft.product.model.Product;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
public interface ProductDao {

    void add(Product product);

    void update(Product product);

    void delete(int id);

    Product load(int id);

}
