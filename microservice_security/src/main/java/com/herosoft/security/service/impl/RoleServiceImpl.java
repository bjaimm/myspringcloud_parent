package com.herosoft.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.herosoft.security.mappers.RoleMapper;
import com.herosoft.security.po.RolePo;
import com.herosoft.security.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RolePo> implements RoleService {
}
