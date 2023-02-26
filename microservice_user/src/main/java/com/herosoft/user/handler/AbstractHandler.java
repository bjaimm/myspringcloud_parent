package com.herosoft.user.handler;

import com.herosoft.commons.dto.UserDto;

public abstract class AbstractHandler {
    abstract public String handler(UserDto userDto);
}
