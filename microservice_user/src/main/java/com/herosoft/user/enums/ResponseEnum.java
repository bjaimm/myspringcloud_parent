package com.herosoft.user.enums;

import lombok.Getter;

@Getter
public enum ResponseEnum {

    /**
     * SUCCESS
     */
    SUUCESS(200,"成功"),
    /**
     * NO_PERMISSION
     */
    NO_PERMISSION(403,"没有权限"),
    /**
     * NO_AUTHENTICATION
     */
    NO_AUTHENTICATION(401,"没有认证"),
    /**
     * NO_RESOURCE
     */
    NO_RESOURCE(404,"没有找到资源"),
    /**
     * INTERNAL_SERVER_ERROR
     */
    INTERNAL_SERVER_ERROR(500,"服务器跑路了")
    ;

    private Integer reponseCode;
    private String reponseMessage;

    ResponseEnum(Integer reponseCode, String reponseMessage) {
        this.reponseCode = reponseCode;
        this.reponseMessage = reponseMessage;
    }
}
