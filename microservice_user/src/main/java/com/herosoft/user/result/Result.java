package com.herosoft.user.result;

import com.herosoft.user.enums.ResponseEnum;
import com.herosoft.user.exceptions.DefinitionException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Boolean success;
    private Integer code;
    private  String message;
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
        result.setMessage(exception.getMessage());
        result.setData(null);

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
