package com.arslinth.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author Arslinth
 * @ClassName AuthenticationUtils
 * @Description 登入账号判断工具
 * @Date 2021/4/3
 */

public class AuthenticationUtils {

    public static String getAuthenticationName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public static Boolean isLogin(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !authentication.getCredentials().equals("");
    }

}
