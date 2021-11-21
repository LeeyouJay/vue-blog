package com.arslinth.config.jwt;

import com.arslinth.config.redis.RedisTool;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Arslinth
 * @ClassName JwtAuthenticationFilter
 * @Description TODO
 * @Date 2021/2/22
 */

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    private RedisTool redisTool;

    //通过构造方法传入redis工具
    public JwtAuthenticationFilter(AuthenticationManager authManager, RedisTool redisTool) {
        super(authManager);
        this.redisTool = redisTool;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!JwtUtil.checkToken(request)) {
            //token不通过规则判断，直接放行到下一条过滤器（此时SecurityContext中没有任何权限，放行后会被最终的过滤器检测到无权限，然后禁止访问）
            chain.doFilter(request, response);
            return;
        }
        //根据token获得authenticationToken
        UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(request);
        //将authenticationToken存入SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        //放行
        chain.doFilter(request, response);

    }

    /**
     * 这里从token中获取用户信息并新建一个UsernamePasswordAuthenticationToken
     */
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {

        String userName = JwtUtil.getUserNameByJwtToken(request);
        String details = JwtUtil.getUserDetailsByJwtToken(request);
        List<String> list = JwtUtil.getAuthoritiesByJwtToken(request);
        if (userName != null) {
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            for (String str : list) {
                if (StringUtils.isEmpty(str)) continue;
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(str);
                authorities.add(authority);
            }
            return new UsernamePasswordAuthenticationToken(userName, details,authorities);
        }
        return new UsernamePasswordAuthenticationToken(null, details);
    }

}
