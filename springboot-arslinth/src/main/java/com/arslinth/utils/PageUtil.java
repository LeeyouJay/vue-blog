package com.arslinth.utils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Arslinth
 * @ClassName PageUtil
 * @Description 自定义分页工具
 * @Date 2021/3/5
 */
public class PageUtil {
    public static <T> List<T> limit(List<T> T, long pageIndex, long pageSize ){
       return T.stream().skip((pageIndex-1)*pageSize).limit(pageSize).collect(Collectors.toList());
    }
}
