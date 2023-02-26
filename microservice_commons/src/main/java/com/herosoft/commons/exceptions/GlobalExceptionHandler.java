package com.herosoft.commons.exceptions;

import com.herosoft.commons.results.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = DefinitionException.class)
    @ResponseBody
    public Result bizExceptionsHandler(DefinitionException definitionException){
        return Result.defineError(definitionException);
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result validExceptionHandler(MethodArgumentNotValidException methodArgumentNotValidException){
        log.info("validExceptionHandler开始执行。。。");
        StringBuilder stringBuilder = new StringBuilder();

        List<ObjectError> errorList = methodArgumentNotValidException.getBindingResult().getAllErrors();

        if(!CollectionUtils.isEmpty(errorList)) {
            stringBuilder.append("参数校验错误[");
            for (ObjectError objectError : errorList) {
                stringBuilder.append(objectError.getDefaultMessage() + ",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.append("]");
        }

        return new Result<>(false,406,stringBuilder.toString(),null);

    }

    //处理其他异常
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public  Result otherExceptionHandler(Exception exception){
        return Result.otherError(exception);
    }
}
