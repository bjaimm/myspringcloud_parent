package com.herosoft.product.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("product_change_log")
public class ProductChangeLogPo {

    @TableId(type= IdType.AUTO)
    private int id;

    @TableField(value = "order_header_id")
    private int orderHeaderId;

    @TableField(value = "order_detail_id")
    private int orderDetailId;

    @TableField(value = "product_id")
    private int productId;

    @TableField(value = "product_qty")
    private int productQty;

    @TableField(value = "status")
    private int status;

    @TableField(value = "create_by")
    private int createBy;

    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_by")
    private int updateBy;

    @TableField(value = "update_time",fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
