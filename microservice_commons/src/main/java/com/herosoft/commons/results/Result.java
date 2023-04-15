package com.herosoft.commons.results;

import com.alibaba.fastjson.annotation.JSONField;
import com.herosoft.commons.exceptions.DefinitionException;
import com.herosoft.commons.enums.ResponseEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {
    @JSONField(ordinal=1)
    private Boolean success;

    @JSONField(ordinal=2)
    private Integer code;

    @JSONField(ordinal=3)
    private  String message;

    @JSONField(ordinal=4)
    private T data;

    //自定义异常返回的结果
    public static Result defineError(DefinitionException definitionException){
        Result result=new Result();

        result.setSuccess(false);
        result.setCode(definitionException.getErrorCode());
        result.setMessage(definitionException.getErrorMessage());
        result.setData(null);

        return result;
    }

    //其他异常返回的结果
    public static Result otherError(Exception exception){
        Result result=new Result();

        result.setSuccess(false);
        result.setCode(ResponseEnum.INTERNAL_SERVER_ERROR.getReponseCode());
        result.setMessage(exception.getLocalizedMessage());
        result.setData(null);

        return result;
    }

    //自定义枚举响应异常
    public static Result responseEnum(ResponseEnum responseEnum){
        Result result=new Result();

        result.setSuccess(false);
        result.setCode(responseEnum.getReponseCode());
        result.setMessage(responseEnum.getReponseMessage());
        result.setData(null);

        return result;
    }

    public static Result responseEnum(ResponseEnum responseEnum, Object data){
        Result result=new Result();

        result.setSuccess(false);
        result.setCode(responseEnum.getReponseCode());
        result.setMessage(responseEnum.getReponseMessage());
        result.setData(data);

        return result;
    }

    public static Result success(Object data){

        Result result = new Result();

        result.setSuccess(true);
        result.setCode(ResponseEnum.SUUCESS.getReponseCode());
        result.setMessage(ResponseEnum.SUUCESS.getReponseMessage());
        result.setData(data);

        return result;
    }
}
