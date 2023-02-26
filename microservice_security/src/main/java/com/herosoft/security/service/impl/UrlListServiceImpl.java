package com.herosoft.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.herosoft.security.mappers.RoleMapper;
import com.herosoft.security.mappers.UrlListMapper;
import com.herosoft.security.po.RolePo;
import com.herosoft.security.po.UrlListPo;
import com.herosoft.security.service.RoleService;
import com.herosoft.security.service.UrlListService;
import org.springframework.stereotype.Service;

@Service
public class UrlListServiceImpl extends ServiceImpl<UrlListMapper, UrlListPo> implements UrlListService {
}
