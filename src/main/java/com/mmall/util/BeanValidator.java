package com.mmall.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.exception.ParamException;
import org.apache.commons.collections.MapUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

/**
 * @Author: zhangyu
 * @Description: 用来校验bean是否符合要求的工具类
 * @Date: in 2019/8/25 14:55
 */
public class BeanValidator {

    //创建校验工程
    private static ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

    /**
     * 校验单个参数类,当参数不符合要求时会抛出ParamException
     */
    public static void check(Object object) throws ParamException {
        Map<String, String> errors = BeanValidator.validateObject(object);
        if(MapUtils.isNotEmpty(errors)){
            throw new ParamException(errors.toString());
        }
    }

    /**
     * 校验单个或多个参数类
     */
    public static Map<String, String> validateObject(Object first, Object... objects){
        if(objects != null && objects.length > 0){
            return validateList(Lists.asList(first, objects));
        }else{
            return validate(first, new Class[0]);
        }
    }

    /**
     * 校验单个Bean
     * key: 有问题的字段
     * value: 错误信息
     */
    public static <T> Map<String, String> validate(T t, Class... groups){
        Validator validator = validatorFactory.getValidator();
        //获取校验结果
        Set validateResult = validator.validate(t, groups);
        if(validateResult.isEmpty()){
            return Collections.emptyMap();
        }else{
            //不为空时说明出错误了
            LinkedHashMap errors = Maps.newLinkedHashMap();
            Iterator iterator = validateResult.iterator();
            while(iterator.hasNext()){
                ConstraintViolation violation = (ConstraintViolation) iterator.next();
                errors.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return errors;
        }
    }

    /**
     * 校验list bean
     * key: 有问题的字段
     * value: 错误信息
     */
    public static Map<String,String> validateList(Collection<?> collection){
        Preconditions.checkNotNull(collection);
        Iterator<?> iterator = collection.iterator();
        Map errors;
        do {
            if(!iterator.hasNext()){
                return Collections.emptyMap();
            }
            Object object = iterator.next();
            errors = validate(object, new Class[0]);
        }while(errors.isEmpty());
        return errors;
    }


}
