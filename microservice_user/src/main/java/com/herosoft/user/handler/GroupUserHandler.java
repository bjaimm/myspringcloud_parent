package com.herosoft.user.handler;

import com.herosoft.user.annotations.HandlerType;
import com.herosoft.user.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
@HandlerType(value = "2")
public class GroupUserHandler extends AbstractHandler{
    @Override
    public String handler(UserDto userDto) {
        System.out.println("执行团购用户处理逻辑。。。"+userDto.toString());

        return "执行团购用户处理逻辑。。。"+userDto.toString();
    }
}
