package com.herosoft.commons.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtPayloadDto {

    @ApiModelProperty("主题")
    private String sub;

    @ApiModelProperty("签发时间")
    private Long iat;

    @ApiModelProperty("过期时间")
    private Long exp;

    @ApiModelProperty("JWT Id")
    private String jti;

    @ApiModelProperty("用户名称")
    private String username;

    @ApiModelProperty("用户Id")
    private Integer userId;

    @ApiModelProperty("用户的权限")
    private List<String> authorities;
}
