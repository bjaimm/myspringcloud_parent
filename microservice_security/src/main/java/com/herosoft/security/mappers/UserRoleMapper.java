package com.herosoft.security.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.herosoft.security.po.UserRolePo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRolePo> {
}
