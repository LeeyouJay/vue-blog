package com.arslinth.common;

/**
 * @author Arslinth
 * @ClassName Constants
 * @Description 常量类
 * @Date 2021/3/6
 */
public class Constants {
    //验证码登入地址
    public static final String MOBILE_LOGIN_URL = "/mobileCodeLogin";
    //验证码登入提交字段
    public static final String FORM_MOBILE_NUM = "mobile";
    public static final String FORM_CODE = "code";
    //redis储存字段后缀
    public static final String FAIL_TIME_SUFFIX = "_fails";
    public static final String MOBILE_CODE_SUFFIX = "_verifyCode";
    public static final String AUTHORITIES_SUFFIX = "_auths";
    public static final String ARTICLE_TAG_SUFFIX = "_article_tags";
    //重置密码123456
    public static final String RESET_CODE = "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92";

}
