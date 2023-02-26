package com.herosoft.security.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.herosoft.security.po.RolePo;
import com.herosoft.security.po.UrlListPo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UrlListMapper extends BaseMapper<UrlListPo> {
}
