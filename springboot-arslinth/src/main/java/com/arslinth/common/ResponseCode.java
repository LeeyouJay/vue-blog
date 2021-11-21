package com.arslinth.common;

/**
 * @author Arslinth
 * @Description TODO
 * @Date 2021/2/22
 */
public interface ResponseCode {

    Integer SUCCESS = 200;

    Integer LOGIN_FAIL = 201;

    //账号不存在
    Integer NO_USER = 2000;
    //账号已被禁用
    Integer IS_LOCKED = 2001;
    //密码不正确
    Integer NO_PASS= 2002;
    //验证失败
    Integer CHECK_CAPTCHA_FAIL = 2003;

    Integer FAIL = 501;
    //访问权限异常
    Integer ACCESS_NOT = 403;
    //token参数异常
    Integer NO_LOGIN = 401;
}
