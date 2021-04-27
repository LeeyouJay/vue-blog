package com.arslinth.utils;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Arslinth
 * @ClassName ResponseUtil
 * @Description TODO
 * @Date 2021/2/22
 */
public class ResponseUtil {
    public static void print(HttpServletResponse response, Object... object) {
        PrintWriter writer = null;
        try {
            writer= utf8AndJson(response).getWriter();
            for (Object o : object) {
                writer.print(o);
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
    }

    private static HttpServletResponse utf8AndJson(HttpServletResponse response) {
        response.setContentType("text/json;charset=utf-8");
        return response;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            System.out.println(RandomUtil.randomString(16));
        }

    }
}
