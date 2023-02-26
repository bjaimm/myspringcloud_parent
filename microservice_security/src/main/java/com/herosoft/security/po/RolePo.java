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
@TableName("role")
public class RolePo {
    @TableId(type= IdType.AUTO,value = "role_id")
    private Integer roleId;

    @TableField(value = "role_name")
    private String roleName;

    @TableField(value = "role_descr")
    private String roleDescr;
}
