package com.herosoft.security.dto;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义反序列处理器，解决Fastjson在反序列化对象字段类型为接口时（有多个实现类），返回null的问题
 */
public class GrantedAuthorityDeserialize implements ObjectDeserializer {
    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {

        JSONObject jsonObject = parser.parseObject();
        List<SimpleGrantedAuthority> grantedAuthority;

        grantedAuthority= JSONObject.toJavaObject(jsonObject,ArrayList.class);

        return (T) grantedAuthority;
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
