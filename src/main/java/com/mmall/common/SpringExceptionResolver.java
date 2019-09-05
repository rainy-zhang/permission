package com.mmall.common;

import com.mmall.exception.ParamException;
import com.mmall.exception.PermissionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: zhangyu
 * @Description: 处理异常
 * @Date: in 2019/8/25 13:55
 */
@Slf4j
public class SpringExceptionResolver implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) {
        String url = request.getRequestURL().toString();
        ModelAndView mv;
        String defaultMsg = "System Error";
        //项目中所有请求json数据都使用.json结尾
        if(url.endsWith(".json")){
            if(e instanceof PermissionException || e instanceof ParamException){
                JsonData result = JsonData.failed(e.getMessage());
                mv = new ModelAndView("jsonView", result.toMap());
            }else{
                log.error("unknown json exception, url: " + url, e);
                JsonData result = JsonData.failed(defaultMsg);
                mv = new ModelAndView("jsonView", result.toMap());
            }
        }else if(url.endsWith(".page")){    //所有请求页面都是用.page结尾
            log.error("unknown page exception, url: " + url, e);
            JsonData result = JsonData.failed(defaultMsg);
            mv = new ModelAndView("exception", result.toMap());
        }else{
            log.error("unknown exception, url: " + url, e);
            JsonData result = JsonData.failed(defaultMsg);
            mv = new ModelAndView("jsonView", result.toMap());
        }
        return mv;
    }
}
