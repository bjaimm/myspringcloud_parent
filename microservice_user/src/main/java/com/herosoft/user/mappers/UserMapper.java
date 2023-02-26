package com.herosoft.user.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.herosoft.commons.dto.UserDto;
import com.herosoft.user.po.UserPo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<UserPo> {
    /**
     * add user
     *
     * @param user user information
     */
    void add(UserPo user);

    /**
     * update user information
     *
     * @param user new user information
     * @return int
     */
    int updateBySql(UserPo user);

    /**
     * delete user by id
     *
     * @param id user id
     */
    void delete(Integer id);

    /**
     * find user by id
     *
     * @param id user id
     * @return User
     */
    UserPo load(Integer id);

    /**
     * find all users
     *
     * @return List<User>
     */
    List<UserPo> findAll();

    List<UserPo> findUsersSelective(UserDto userCriteria);

    void saveBatchBySql(List<UserPo> userList);
}
