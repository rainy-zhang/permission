package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.codehaus.jackson.type.TypeReference;

/**
 * @Author: zhangyu
 * @Description: 用来将对象转换为Json字符串或者将字符串转换为对象
 * @Date: in 2019/8/25 15:48
 */
@Slf4j
public class JsonMapper {

    private static ObjectMapper objectMapper = new ObjectMapper();

    //config
    static{
        //排除掉为空的字段
        objectMapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.setFilters(new SimpleFilterProvider().setFailOnUnknownId(false));
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
    }

    /**
     * 将对象转换为字符串
     */
    public static <T> String object2String(T src) {
        if(src == null)
            return null;
        try{
            return src instanceof String ? (String) src : objectMapper.writeValueAsString(src);
        } catch (Exception e){
            log.warn("parse object to string exception, error:{}", e);
            return null;
        }
    }

    /**
     * 将String转为Object
     */
    public static <T> T string2Object(String src, TypeReference<T> typeReference) {
        if(src == null || typeReference == null)
            return null;
        try {
            return  (T) (typeReference.getType().equals(String.class) ? src : objectMapper.readValue(src, typeReference));
        } catch (Exception e){
            log.warn("parse string to object exception, String:{}, TypeReference<T>:{}, error:{}", src, typeReference.getType(), e);
            return null;
        }
    }


}
