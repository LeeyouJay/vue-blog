package com.arslinth.utils;

import com.arslinth.entity.SysLog;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Arslinth
 * @ClassName SysLogUtils
 * @Description TODO
 * @Date 2021/4/25
 */
public class SysLogUtils {

    public static SysLog getIpSource(HttpServletRequest request){
        String ipAddress = request.getParameter("ipAddress");
        String ipSource = request.getParameter("ipSource");
        String browserName = request.getParameter("browserName");
        String systemName = request.getParameter("systemName");
        return SysLog.builder().ipSource(ipSource)
                .browserName(browserName)
                .systemName(systemName)
                .ipAddress(ipAddress).build();
    }
}
