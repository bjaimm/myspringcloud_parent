package com.herosoft.user.handler;

import com.herosoft.user.annotations.HandlerType;
import com.herosoft.user.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
@HandlerType(value = "1")
public class NormalUserHandler extends AbstractHandler{
    @Override
    public String handler(UserDto userDto) {
        System.out.println("执行普通用户处理逻辑。。。"+userDto.toString());
        return "执行普通用户处理逻辑。。。"+userDto.toString();
    }
}
