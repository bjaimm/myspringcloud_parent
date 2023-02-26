package com.herosoft.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.herosoft.security.mappers.RoleMapper;
import com.herosoft.security.mappers.RolePermissionMapper;
import com.herosoft.security.po.RolePermissionPo;
import com.herosoft.security.po.RolePo;
import com.herosoft.security.service.RolePermissionService;
import com.herosoft.security.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermissionPo>
        implements RolePermissionService {

    @Override
    public Map<String, List<String>> findAllAuthorities() {
        Map<String, List<String>> authorities=new HashMap<>();

        getBaseMapper().findAllAuthorities().forEach(item->{
            authorities.put(item.getUrl(), Arrays.asList(item.getAuthorities().split(",")));
        });
        return authorities;
    }
}
