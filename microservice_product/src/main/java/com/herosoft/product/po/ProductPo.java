package com.herosoft.product.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("product")
public class ProductPo {
    @TableId(type= IdType.AUTO)
    @TableField(value="productid")
    private Integer productId;

    @TableField(value="productname")
    @NotNull(message = "产品名称不能为空")
    private String productName;

    @NotNull(message = "产品价格不能为空")
    @TableField(value="productprice")
    private Double productPrice;

    @NotNull(message = "产品数量不能为空")
    @TableField(value="productamount")
    private Integer productAmount;
}
