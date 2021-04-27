package com.arslinth.common;

/**
 * @author Arslinth
 * @Description TODO
 * @Date 2021/2/22
 */
public interface ResponseCode {

    public static Integer SUCCESS = 200;

    public static Integer LOGIN_FAIL = 201;

    //账号不存在
    public static Integer NO_USER = 2000;
    //账号已被禁用
    public static Integer IS_LOCKED = 2001;
    //密码不正确
    public static Integer NO_PASS= 2002;
    //验证失败
    public static Integer CHECK_CAPTCHA_FAIL = 2003;

    public static Integer FAIL = 501;
    //访问权限异常
    public static Integer ACCESS_NOT = 403;
    //token参数异常
    public static Integer NO_LOGIN = 401;
}
