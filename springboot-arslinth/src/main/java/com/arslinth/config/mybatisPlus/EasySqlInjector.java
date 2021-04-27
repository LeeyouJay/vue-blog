package com.arslinth.config.mybatisPlus;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;

import java.util.List;

/**
 * @author Arslinth
 * @ClassName EasySqlInjector
 * @Description TODO
 * @Date 2021/2/27
 */
public class EasySqlInjector extends DefaultSqlInjector {

    //添加in的查询方法
    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass);
        methodList.add(new InsertBatchSomeColumn());
        return methodList;
    }

}
