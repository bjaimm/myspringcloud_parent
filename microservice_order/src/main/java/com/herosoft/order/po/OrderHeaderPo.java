package com.herosoft.order.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@TableName("order_header")
@AllArgsConstructor
@NoArgsConstructor
public class OrderHeaderPo {
    @TableId(type = IdType.AUTO)
    @TableField(value = "order_header_id")
    private Integer orderHeaderId;
    @TableField(value = "user_id")
    private Integer userId;
    @TableField(value = "order_amount")
    private Double orderAmount;

    @TableField(value = "status")
    private int status;
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
        return "OrderHeaderPo{" +
                "orderHeaderId=" + orderHeaderId +
                ", userId=" + userId +
                ", orderAmount=" + orderAmount +
                ", status=" + status +
                ", createBy=" + createBy +
                ", createTime=" + createTime +
                ", updateBy=" + updateBy +
                ", updateTime=" + updateTime +
                '}';
    }
}
