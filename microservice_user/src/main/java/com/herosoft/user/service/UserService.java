package com.herosoft.user.service;

import com.herosoft.user.dao.UserDao;
import com.herosoft.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public List<User> findAll(){
        return userDao.findAll();
    }

    public User findById(Integer id){
        return userDao.findById(id).get();
    }

    public void add(User user){
        userDao.save(user);
    }

    public void update(User user){
        userDao.save(user);
    }

    public void delete(Integer id){
        userDao.deleteById(id);
    }
}
