package com.herosoft.user.exceptions;

import com.herosoft.commons.exceptions.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
@Slf4j
public class UserGlobalExceptionHandler extends GlobalExceptionHandler {
}
