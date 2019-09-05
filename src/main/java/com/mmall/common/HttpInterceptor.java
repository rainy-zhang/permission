package com.mmall.common;

import com.mmall.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Author: zhangyu
 * @Description: 请求监听器
 * @Date: in 2019/8/25 16:32
 */
@Slf4j
public class HttpInterceptor extends HandlerInterceptorAdapter {

    private static final String START_TIME = "requestStartTime";

    //处理请求前执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        Map params = request.getParameterMap();
        log.info("request start uri:{}, params:{}", uri, JsonMapper.object2String(params));
        long start = System.currentTimeMillis();
        request.setAttribute(START_TIME, start);
        return true;
    }

    //请求正常结束后执行
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        String uri = request.getRequestURI();
//        Long start = (Long) request.getAttribute(START_TIME);
//        long end = System.currentTimeMillis();
//        log.info("request finished uri:{}, cost:{}ms", uri, end - start);
        removeThreadLocalInfo();
    }

    //任何情况下(包括异常情况)都会调用
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String uri = request.getRequestURI();
        Long start = (Long) request.getAttribute(START_TIME);
        long end = System.currentTimeMillis();
        log.info("request complete uri:{}, cost:{}ms", uri, end - start);
        //请求执行结束后删除ThreadLocal中的信息, 防治内存溢出
        removeThreadLocalInfo();
    }

    /**
     * 移除ThreadLocal中的信息
     */
    public void removeThreadLocalInfo () {
        RequestHolder.remove();
    }
}
