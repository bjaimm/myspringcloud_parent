package com.herosoft.security.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.herosoft.security.po.PermissionPo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PermissionMapper extends BaseMapper<PermissionPo> {
}
