package com.arslinth.config.jwt;

import com.alibaba.fastjson.JSON;
import com.arslinth.common.ApiResponse;
import com.arslinth.common.ResponseCode;
import com.arslinth.entity.SysUser;
import com.arslinth.exception.MyAccountException;
import com.arslinth.service.SysLogService;
import com.arslinth.utils.ResponseUtil;
import com.arslinth.utils.SysLogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
 * @ClassName JwtLoginFilter
 * @Description jwt登入过滤器，继承UsernamePasswordAuthenticationFilter，对用户名密码进行登录校验；含认证成功和失败后执行的方法
 * @Date 2021/2/22
 */
@Slf4j
public class JwtLoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private SysLogService sysLogService;

    public JwtLoginAuthenticationFilter(AuthenticationManager authenticationManager, SysLogService sysLogService) {
        this.setPostOnly(true);
        this.sysLogService = sysLogService;
        this.setAuthenticationManager(authenticationManager);

    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException {

        //从请求中读取数据
        String username = obtainUsername(req);
        String password = obtainPassword(req);
        //两种抛异常方法
        if (StringUtils.isEmpty(username)) {
            //自定义AccountException
            throw new MyAccountException("账号为空");
        }
        //security中AuthenticationException的自带异常
        if (StringUtils.isEmpty(password)) {
            throw new BadCredentialsException("密码为空");
        }
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        return getAuthenticationManager().authenticate(token);
    }

    /**
     * 登录成功
     * 成功后创建token返回前端
     * key为username
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authentication) throws IOException, ServletException {
        SysUser user = (SysUser) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> sysAuthorities = new ArrayList<>();
        authorities.forEach(item-> sysAuthorities.add(item.getAuthority()));
        String username = user.getUsername();
        String email = user.getEmail();
        String jwtToken = JwtUtil.getJwtToken(username,email,sysAuthorities);
        //返回并保存信息
        sysLogService.saveSysLog(request,"账号密码验证","登录成功！",true,username);
        ResponseUtil.print(response, JSON.toJSONString(ApiResponse.code(ResponseCode.SUCCESS)
                .message("登录成功！").data("token", jwtToken).data("nickName",user.getNickName()).data("avatar",user.getAvatar())));

    }

    /**
     * 登录失败
     * 失败后提示用户登录失败
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException e) throws IOException, ServletException {
        MyAccountException ex = (MyAccountException)e;
        String username = obtainUsername(request);

        sysLogService.saveSysLog(request,"账号密码验证",ex.getMessage(),false,username);
        log.warn(e.getMessage());
        ResponseUtil.print(response, JSON.toJSONString(ApiResponse.code(ex.getCode()).message(ex.getMessage())));
    }
}
