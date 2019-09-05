package com.mmall.filter;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.mmall.common.ApplicationContextHelper;
import com.mmall.common.JsonData;
import com.mmall.common.RequestHolder;
import com.mmall.model.SysUser;
import com.mmall.service.SysCoreService;
import com.mmall.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @Author: zhangyu
 * @Description: 做权限拦截的过滤器
 * @Date: in 2019/9/5 15:32
 */
@Slf4j
public class AclControlFilter implements Filter {

    //用来存储需要放行的url
    private static Set<String> exclusionUrlSet = Sets.newConcurrentHashSet();

    //无权访问时跳转的路径
    private static final String noAuthUrl = "/sys/user/noAuth.page";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //获取配置文件中定义的放行路径
        String exclusionUrls = filterConfig.getInitParameter("exclusionUrls");
        List<String> exclusionUrlList = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(exclusionUrls);
        exclusionUrlSet = Sets.newConcurrentHashSet(exclusionUrlList);
        exclusionUrlSet.add(noAuthUrl);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String url = request.getServletPath();
        Map parameterMap = request.getParameterMap();

        if(exclusionUrlSet.contains(url)){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        SysUser user = RequestHolder.getCurrentUser();
        if(user == null){
            log.info("some one visit {}, but no login, parameter:{},", url, JsonMapper.object2String(parameterMap));
            noAuth(request, response);
            return;
        }

        SysCoreService coreService = ApplicationContextHelper.popBean(SysCoreService.class);
        if(!coreService.hasUrlAcl(url)) {
            log.info("{} visit {}, but no acl, parameter:{},", JsonMapper.object2String(user), url, JsonMapper.object2String(parameterMap));
            noAuth(request, response);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    //处理无权访问时的逻辑
    private void noAuth(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String url = request.getServletPath();
        if(url.endsWith(".json")){
            JsonData jsonData = JsonData.failed("无权访问,如有疑问请联系管理员!");
            response.setHeader("Content-Type", "application/json");
            PrintWriter out = response.getWriter();
            out.print(JsonMapper.object2String(jsonData));
            out.flush();
            out.close();
            return;
        }else{
            clientRedirect(noAuthUrl, response);
            return;
        }
    }

    //跳转到指定的url
    private void clientRedirect(String url, HttpServletResponse response) throws IOException {
        response.setHeader("Content-Type", "text/html");
        PrintWriter out = response.getWriter();
        out.print("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + "<head>\n" + "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"/>\n"
                + "<title>跳转中...</title>\n" + "</head>\n" + "<body>\n" + "跳转中，请稍候...\n" + "<script type=\"text/javascript\">//<![CDATA[\n"
                + "window.location.href='" + url + "?ret='+encodeURIComponent(window.location.href);\n" + "//]]></script>\n" + "</body>\n" + "</html>\n");
        out.flush();
        out.close();
    }

    @Override
    public void destroy() {

    }
}
