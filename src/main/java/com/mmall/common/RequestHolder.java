package com.mmall.common;

import com.mmall.model.SysUser;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: zhangyu
 * @Description: ThreadLocal 各个线程之间保存的内容是分开的,每个线程会处理自己单独的内容.这里将request放到ThreadLocal中每次获取时可以获取到自己进程的request
 * @Date: in 2019/9/1 12:41
 */
public class RequestHolder {

    //使用ThreadLocal存放当前用户的信息
    private static final ThreadLocal<SysUser> userHolder = new ThreadLocal<>();

    //存放request的ThreadLocal
    private static final ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<>();

    /**
     * 将SysUser对象放入ThreadLocal中
     */
    public static void add(SysUser user){
        userHolder.set(user);
    }

    /**
     * 将Request对象放入ThreadLocal中
     */
    public static void add(HttpServletRequest request){
        requestHolder.set(request);
    }

    /**
     * 从ThreadLocal中获取当前SysUser对象
     */
    public static SysUser getCurrentUser(){
        return userHolder.get();
    }

    /**
     * 从ThreadLocal中获取当前Request对象
     */
    public static HttpServletRequest getCurrentRequest(){
        return requestHolder.get();
    }

    /**
     * 从ThreadLocal中移除SysUser对象和Request对象
     */
    public static void remove(){
        userHolder.remove();
        requestHolder.remove();
    }


}
