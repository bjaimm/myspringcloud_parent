package com.herosoft.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.herosoft.commons.dto.UserDto;
import com.herosoft.user.handler.AbstractHandler;
import com.herosoft.user.handler.HandlerContext;
import com.herosoft.user.service.UserService;
import com.herosoft.user.mappers.UserMapper;
import com.herosoft.user.po.UserPo;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserPo>
        implements InitializingBean, DisposableBean, UserService {

    //@Autowired
    //private UserDao userDao;

    @Autowired
    private  HandlerContext handlerContext;

    @Autowired
    private UserMapper userMapper;

    public List<UserPo> findAll(){
        //return userDao.findAll(); 这是JPA方式
        return userMapper.findAll();
    }

    public UserPo findById(Integer id){
        //return userDao.findById(id).get(); 这是JPA方式
        return userMapper.load(id);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void add(UserPo user){
        //userDao.save(user);这是JPA方式
        userMapper.add(user);
        //int temp = 10/0;
    }

    //直接调用本类的其他事务方法B时，即使事务方法B有异常，事务也不会回滚
    public void add1(UserPo user){

        add(user);

    }

    public void update(UserPo user){
        //userDao.save(user); 这是JPA方式
        userMapper.updateBySql(user);
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

    @Override
    public void destroy() throws Exception {
        System.out.println("这里可以在容器Destroy前，执行一些自定义的事情");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("这里可以在容器创建Bean后，执行一些自定义的初始化。。。");
    }

    @PostConstruct
    public void init(){
        System.out.println("这里可以通过@PostConstructor执行一些Bean的初始化逻辑。。。");
    }

    @Override
    public List<UserPo> findUsersSelective(UserDto userCriteria) {
        return userMapper.findUsersSelective(userCriteria);
    }

    @Override
    public void saveBatchBySql(List<UserPo> userList) {
        userMapper.saveBatchBySql(userList);
    }

    @Override
    public int reduceBalance(int userId, double reducedBalance) {
        UserPo userPo = Optional.ofNullable(userMapper.selectById(userId)).orElse(new UserPo());

        if(userPo.getId()<=0 || userPo.getId() == null){
            return 0;
        }
        double currentBalance = userPo.getBalance();

        UserPo userPoUpdate = userPo;
        userPoUpdate.setBalance(currentBalance-reducedBalance);

        UpdateWrapper<UserPo> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.eq("id",userId);

        return userMapper.update(userPoUpdate,userUpdateWrapper);

    }

    @Override
    public UserPo findByUserName(String userName) {
        return userMapper.selectOne(Wrappers.lambdaQuery(UserPo.class)
                .eq(UserPo::getUsername,userName));
    }

}
