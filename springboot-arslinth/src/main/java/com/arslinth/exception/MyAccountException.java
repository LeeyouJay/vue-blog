package com.arslinth.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author Arslinth
 * @ClassName AccountException
 * @Description 自定义异常类
 * @Date 2021/2/22
 */
public class MyAccountException extends AuthenticationException {

    private int code;

    public MyAccountException(String msg) {
        super(msg);
    }

    public MyAccountException(int code,String msg) {
        super(msg);
        this.code =code;
    }

    public int getCode() {
        return code;
    }
}
