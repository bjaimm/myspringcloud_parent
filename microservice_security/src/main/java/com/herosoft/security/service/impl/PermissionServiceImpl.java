package com.herosoft.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.herosoft.security.mappers.PermissionMapper;
import com.herosoft.security.mappers.RoleMapper;
import com.herosoft.security.po.PermissionPo;
import com.herosoft.security.po.RolePo;
import com.herosoft.security.service.PermissionService;
import com.herosoft.security.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, PermissionPo>
        implements PermissionService {
}
