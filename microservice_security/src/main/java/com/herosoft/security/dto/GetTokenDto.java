package com.herosoft.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class GetTokenDto {
    private String userName;
    private String password;
    private String clientId;
    private String clientSecret;
    private String grantType;
}
