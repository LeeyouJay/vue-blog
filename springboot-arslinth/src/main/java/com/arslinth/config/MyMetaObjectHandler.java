package com.arslinth.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Arslinth
 * @ClassName MyMetaObjectHandler
 * @Description 数据库自动填充
 * @Date 2021/2/23
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void insertFill(MetaObject metaObject) {
        String date = format.format(new Date());
        this.setFieldValByName("createTime" ,date,metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        String date = format.format(new Date());
        this.setFieldValByName("updateTime" ,date,metaObject);
    }
}
