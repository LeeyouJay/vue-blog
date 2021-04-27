package com.arslinth.config.handler;

import com.alibaba.fastjson.JSON;
import com.arslinth.common.ApiResponse;
import com.arslinth.common.ResponseCode;
import com.arslinth.config.jwt.JwtUtil;
import com.arslinth.utils.ResponseUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Arslinth
 * @ClassName JwtAuthorizedEntryPoint
 * @Description 统一处理 AuthenticationException异常
 * @Date 2021/2/22
 */
@Component
public class JwtAuthorizedEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        if(JwtUtil.checkToken(request))
            ResponseUtil.print(response, JSON.toJSONString(ApiResponse.code(ResponseCode.ACCESS_NOT).message("您的访问权限不足！")));
        else
            ResponseUtil.print(response, JSON.toJSONString(ApiResponse.code(ResponseCode.NO_LOGIN).message("登入信息已失效，请重新登入")));
    }
}
