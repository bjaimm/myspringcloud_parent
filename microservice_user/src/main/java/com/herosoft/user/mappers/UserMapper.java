package com.herosoft.user.mappers;

import com.herosoft.user.pojo.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    /**
     * add user
     *
     * @param user user information
     */
    void add(User user);

    /**
     * update user information
     *
     * @param user new user information
     * @return int
     */
    int update(User user);

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
    User load(Integer id);

    /**
     * find all users
     *
     * @return List<User>
     */
    List<User> findAll();
}
