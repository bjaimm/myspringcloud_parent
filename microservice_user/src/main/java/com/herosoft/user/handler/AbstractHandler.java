package com.herosoft.user.handler;

import com.herosoft.user.dto.UserDto;

public abstract class AbstractHandler {
    abstract public String handler(UserDto userDto);
}
