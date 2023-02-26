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
@TableName(value = "urllist")
public class UrlListPo {

    @TableId(type= IdType.ASSIGN_ID)
    private Integer id;

    @TableField(value = "list_type")
    private Integer listType;

    @TableField(value = "url")
    private String url;
}
