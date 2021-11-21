package com.arslinth.exception;

import org.springframework.security.core.AuthenticationException;

import java.util.Map;

/**
 * @author Arslinth
 * @ClassName AccountException
 * @Description 自定义异常类
 * @Date 2021/2/22
 */
public class MyAccountException extends AuthenticationException {

    private int code;

    private Map<String,Object> data;

    public MyAccountException(String msg) {
        super(msg);
    }

    public MyAccountException(int code,String msg) {
        super(msg);
        this.code =code;
    }

    public MyAccountException(int code,Map data,String msg) {
        super(msg);
        this.code =code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }
    public Map<String,Object> getData(){
        return data;
    }
}
