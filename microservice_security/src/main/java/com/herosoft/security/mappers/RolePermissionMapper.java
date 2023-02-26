package com.herosoft.security.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.herosoft.security.dto.SysUrlAuthorityDto;
import com.herosoft.security.po.RolePermissionPo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermissionPo> {
    List<SysUrlAuthorityDto> findAllAuthorities();
}
