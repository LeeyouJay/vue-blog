package com.arslinth.exception;

/**
 * @author Arslinth
 * @ClassName TransactionalException
 * @Description 自定义事务异常
 * @Date 2021/4/20
 */
public class TransactionalException extends Exception{
    public TransactionalException(String msg) {
        super(msg);
    }
}
