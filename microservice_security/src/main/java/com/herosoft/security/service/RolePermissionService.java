package com.herosoft.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.herosoft.security.po.PermissionPo;
import com.herosoft.security.po.RolePermissionPo;

import java.util.List;
import java.util.Map;

public interface RolePermissionService extends IService<RolePermissionPo> {
    Map<String, List<String>> findAllAuthorities();
}
