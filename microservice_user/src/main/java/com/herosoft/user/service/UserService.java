package com.herosoft.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.herosoft.commons.dto.UserDto;
import com.herosoft.user.po.UserPo;

import java.util.List;

public interface UserService extends IService<UserPo> {
    List<UserPo> findUsersSelective(UserDto userCriteria);

    void saveBatchBySql(List<UserPo> userList);

    int reduceBalance(int userId,double reducedBalance);

    UserPo findByUserName(String userName);
}
