package com.herosoft.security.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("permission")
public class PermissionPo {
    @TableId(type= IdType.AUTO,value = "permission_id")
    private Integer permissionId;

    @TableField(value = "permission_name")
    private String permissionName;

    @TableField(value = "url")
    private String url;
}
