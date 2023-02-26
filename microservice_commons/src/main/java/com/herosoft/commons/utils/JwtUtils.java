package com.herosoft.commons.utils;

import cn.hutool.json.JSONUtil;
import com.herosoft.commons.dto.JwtPayloadDto;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
public class JwtUtils {
    public final static String JWT_HMAC_SECRET="adsjfdskfjdsalfjdslkfjdskfjdkjfdk";
    public String generateJWTByHMAC(String payloadParam, String secret) throws JOSEException {
        //创建JWS头，设置加密算法和类型
        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.HS256)
                .type(JOSEObjectType.JWT)
                .build();

        //封装Payload
        Payload payload = new Payload(payloadParam);

        //创建JWS对象
        JWSObject jwsObject = new JWSObject(jwsHeader,payload);

        //创建HMAC签名器
        JWSSigner jwsSigner = new MACSigner(secret);

        //签名
        jwsObject.sign(jwsSigner);

        return jwsObject.serialize();
    }

    public JwtPayloadDto verifyTokenByHMAC(String token, String secret) throws ParseException, JOSEException {
        //从Toke中获取JWS对象
        JWSObject jwsObject = JWSObject.parse(token);

        //创建JWS验证器
        JWSVerifier jwsVerifier = new MACVerifier(secret);

        if(!jwsObject.verify(jwsVerifier)){
            throw new JOSEException("Token签名不合法！");
        }

        String payload=jwsObject.getPayload().toString();

        JwtPayloadDto payloadDto = JSONUtil.toBean(payload, JwtPayloadDto.class);

        if(payloadDto.getExp()<System.currentTimeMillis()){
            throw new JOSEException("Token已过期！");
        }

        return payloadDto;
    }
}
