package com.herosoft.user.service;

import com.herosoft.user.dao.UserDao;
import com.herosoft.user.dto.UserDto;
import com.herosoft.user.handler.AbstractHandler;
import com.herosoft.user.handler.HandlerContext;
import com.herosoft.user.mappers.UserMapper;
import com.herosoft.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private  HandlerContext handlerContext;

    @Autowired
    private UserMapper userMapper;

    public List<User> findAll(){
        //return userDao.findAll(); 这是JPA方式
        return userMapper.findAll();
    }

    public User findById(Integer id){
        //return userDao.findById(id).get(); 这是JPA方式
        return userMapper.load(id);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void add(User user){
        //userDao.save(user);这是JPA方式
        userMapper.add(user);
        //int temp = 10/0;
    }

    //直接调用本类的其他事务方法B时，即使事务方法B有异常，事务也不会回滚
    public void add1(User user){

        add(user);

    }

    public void update(User user){
        //userDao.save(user); 这是JPA方式
        userMapper.update(user);
    }

    public void delete(Integer id){
        //userDao.deleteById(id);这是JPA方式
        userMapper.delete(id);
    }

    public String handler(UserDto userDto){
        System.out.println("UserService method handler is called...");
        AbstractHandler handler= handlerContext.getInstance(userDto.getUserType());
        return handler.handler(userDto);
    }
}
