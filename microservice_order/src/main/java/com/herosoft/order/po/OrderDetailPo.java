package com.herosoft.order.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@TableName("order_detail")
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailPo {
    @TableId(type = IdType.AUTO)
    @TableField(value = "order_detail_id")
    private Integer orderDetailId;
    @TableField(value = "order_header_id")
    private Integer orderHeaderId;
    @TableField(value = "product_id")
    private Integer productId;
    @TableField(value = "product_qty")
    private Integer productQty;
    @TableField(value = "product_price")
    private Double productPrice;
    @TableField(value = "create_by")
    private Integer createBy;
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(value = "update_by")
    private Integer updateBy;
    @TableField(value = "update_time",fill=FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Override
    public String toString() {
        return "OrderDetail{" +
                "orderDetailId=" + orderDetailId +
                ", orderHeaderId=" + orderHeaderId +
                ", productId=" + productId +
                ", productQty=" + productQty +
                ", productPrice=" + productPrice +
                ", createBy=" + createBy +
                ", createTime=" + createTime +
                ", updateBy=" + updateBy +
                ", updateTime=" + updateTime +
                '}';
    }
}
