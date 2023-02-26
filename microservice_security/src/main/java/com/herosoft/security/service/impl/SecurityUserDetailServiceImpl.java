package com.herosoft.security.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.herosoft.commons.dto.UserDto;
import com.herosoft.security.clients.UserService;
import com.herosoft.security.dto.SecurityUserDetails;
import com.herosoft.security.po.PermissionPo;
import com.herosoft.security.po.RolePermissionPo;
import com.herosoft.security.po.RolePo;
import com.herosoft.security.po.UserRolePo;
import com.herosoft.security.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SecurityUserDetailServiceImpl implements SecurityUserDetailService {
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private UserRoleService userRoleService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        log.info("loadUserByUsername调用参数：{}",s);

        UserDto userDto = JSON.parseObject(JSON.toJSONString(userService.findById(Integer.parseInt(s)).getData()),
                UserDto.class);

        if (BeanUtil.isEmpty(userDto)) {
            throw new UsernameNotFoundException("用户不存在");
        }

        List<Integer> roleIds = userRoleService.list(Wrappers.lambdaQuery(UserRolePo.class)
                .eq(UserRolePo::getUserId,Integer.parseInt(s))).stream()
                .map(
                userRolePo -> userRolePo.getRoleId()
                )
                .distinct()
                .collect(Collectors.toList());

        List<Integer> permissionIds = roleIds.stream().flatMap(roleId ->
                        rolePermissionService.list(Wrappers.lambdaQuery(RolePermissionPo.class)
                                                    .eq(RolePermissionPo::getRoleId,roleId)).stream()
                                .map(rolePermissionPo-> rolePermissionPo.getPermissionId()))
                                .collect(Collectors.toList());

        List<RolePo> roleList = roleService.listByIds(roleIds);

        List<PermissionPo> permissionList = permissionService.listByIds(permissionIds);

        //添加Role
        List<GrantedAuthority> authorities = roleList.stream()
                .map(rolePo-> new SimpleGrantedAuthority(rolePo.getRoleName()))
                .collect(Collectors.toList());

        //添加Permission
        authorities.addAll(permissionList.stream()
                .map(rolePo-> new SimpleGrantedAuthority(rolePo.getPermissionName()))
                .collect(Collectors.toList()));

        return new SecurityUserDetails(authorities,
                userDto.getUserName(),
                userDto.getPassword(),
                userDto.getBalance(),
                userDto.getUserId(),
                true,
                true,
                true,
                true);
    }
}
