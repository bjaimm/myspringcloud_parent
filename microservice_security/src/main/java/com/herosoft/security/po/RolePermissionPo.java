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
@TableName("role_permission")
public class RolePermissionPo {
    @TableId(type= IdType.AUTO)
    private Integer id;

    @TableField(value = "permission_id")
    private Integer permissionId;

    @TableField(value = "role_id")
    private Integer roleId;
}
