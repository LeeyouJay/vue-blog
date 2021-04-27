package com.arslinth.config.handler;

import com.alibaba.fastjson.JSON;
import com.arslinth.common.ApiResponse;
import com.arslinth.common.ResponseCode;
import com.arslinth.config.jwt.JwtUtil;
import com.arslinth.config.redis.RedisTool;
import com.arslinth.utils.ResponseUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Arslinth
 * @ClassName JwtLogoutHandler
 * @Description 退出登入处理
 * @Date 2021/2/22
 */
public class JwtLogoutHandler implements LogoutHandler {

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        ResponseUtil.print(response, JSON.toJSONString(ApiResponse.code(ResponseCode.SUCCESS).message("退出登入成功！")));
    }
}
