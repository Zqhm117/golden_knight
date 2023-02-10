package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否登录
 */
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        //获取本次URI请求
        String requestURI = httpServletRequest.getRequestURI();
        log.info("检查（过滤）到请求:{}",requestURI);
        //设置不需要经过过滤器的路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**"
        };
        //判断请求是否需要处理
        boolean flag = check(urls,requestURI);
        if(flag){
            log.info("本次请求{}不需要处理",requestURI);
            chain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }
        //已登录，则不需要处理
        if(httpServletRequest.getSession().getAttribute("employee") != null){
            log.info("用户已登录，用户id为：{}",httpServletRequest.getSession().getAttribute("employee"));
            chain.doFilter(request,response);
            return;
        }
        //未登录，返回提示信息
        log.info("用户未登录");
        httpServletResponse.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 检查路径是否放行
     * @param urls
     * @param requestURI
     * @return
     */
    private boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url,requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
