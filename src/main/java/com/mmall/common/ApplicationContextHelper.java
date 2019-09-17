package com.mmall.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Author: zhangyu
 * @Description: 获取上下文中的bean
 * @Date: in 2019/8/25 16:15
 */
@Component("applicationContextHelper")
public class ApplicationContextHelper implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        applicationContext = ac;
    }

    /**
     * 获取Application上下文中的bean
     */
    public static <T> T popBean(Class<T> clazz) {
        if(applicationContext == null){
            return null;
        }
        return applicationContext.getBean(clazz);
    }

    /**
     * 获取Application上下文中的bean
     */
    public static <T> T popBean(String name, Class<T> clazz) {
        if(applicationContext == null){
            return null;
        }
        return applicationContext.getBean(name, clazz);
    }

}
