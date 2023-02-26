package com.herosoft.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.herosoft.security.mappers.UserRoleMapper;
import com.herosoft.security.po.UserRolePo;
import com.herosoft.security.service.UserRoleService;
import org.springframework.stereotype.Service;

@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRolePo>
        implements UserRoleService {
}
