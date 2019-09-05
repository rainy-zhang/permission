package com.mmall.controller;

import com.mmall.model.SysUser;
import com.mmall.service.SysUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: zhangyu
 * @Description: 处理用户行为
 * @Date: in 2019/8/28 19:07
 */
@Controller
public class UserController {

    @Resource
    private SysUserService userService;

    @RequestMapping("/logout.page")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.getSession().invalidate();
        String path = "signin.jsp";
        request.getRequestDispatcher(path).forward(request,response);
    }

    @RequestMapping("/login.page")
    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String ret = request.getParameter("ret");
        String errorMsg = "";
        SysUser user = userService.findByKeyword(username);
        if(StringUtils.isBlank(username)){
            errorMsg = "用户名不能为空";
        }else if(StringUtils.isBlank(password)){
            errorMsg = "密码不能为空";
        }else if(user == null){
            errorMsg = "未找到指定的用户!";
        }else if(!MD5Util.encrypt(password).equals(user.getPassword())){
            errorMsg = "用户名或密码错误!";
        }else if(user.getStatus() != 1){
            errorMsg = "用户已被冻结,如有疑问请联系管理员!";
        }else{
            request.getSession().setAttribute("user", user);
            if(StringUtils.isNotBlank(ret)){
                response.sendRedirect(ret);
                return;
            }else{
                response.sendRedirect("/admin/index.page");
                return;
            }
        }
        request.setAttribute("errorMsg", errorMsg);
        request.setAttribute("username", username);
        if(StringUtils.isNotBlank(ret)){
            request.setAttribute("ret", ret);
        }
        String path = "signin.jsp";
        request.getRequestDispatcher(path).forward(request, response);
        return;
    }

}
