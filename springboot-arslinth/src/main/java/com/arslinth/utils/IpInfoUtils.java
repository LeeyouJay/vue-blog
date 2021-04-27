package com.arslinth.utils;

import cn.hutool.core.io.resource.ClassPathResource;
import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.UserAgent;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Arslinth
 * @ClassName IpInfoUtils
 * @Description IP详情获取工具
 * @Date 2021/3/4
 */
public class IpInfoUtils {

    private final static String LOCAL_HOST = "127.0.0.1";

    public static final String REGION = "内网IP|内网IP";

    public static final String INTRANET_IP = "内网IP";

    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (LOCAL_HOST.equals(ip)) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ip = inet.getHostAddress();
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }

        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = LOCAL_HOST;
        }

        return ip;
    }

    //获取客户端主机名称
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
        }
        return "未知";
    }

    //获取ip地址
    public static String getipSource(String ip) throws Exception{
        DbConfig config = new DbConfig();
        String path = "config/ip2region.db";
        String name = "ip2region.db";
        File file = inputStreamToFile(new ClassPathResource(path).getStream(), name);
        DbSearcher searcher = new DbSearcher(config, file.getPath());
        DataBlock dataBlock = searcher.btreeSearch(ip);
        String address = dataBlock.getRegion().replace("0|","");
        if(address.charAt(address.length()-1) == '|'){
            address = address.substring(0,address.length() - 1);
        }
        return address.equals(REGION) ? INTRANET_IP : address;
    }

    //获取浏览器名称
    public static String getBrowser(HttpServletRequest request){
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        Browser browser = userAgent.getBrowser();
        return browser.getName();
    }

    //获取系统名称
    public static String getSystemName(HttpServletRequest request){
        //转成UserAgent对象
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        return userAgent.getOperatingSystem().getName();
    }
    public static File inputStreamToFile(InputStream ins, String name) throws Exception{
        File file = new File(System.getProperty("java.io.tmpdir") + File.separator + name);
        if (file.exists()) {
            return file;
        }
        OutputStream os = new FileOutputStream(file);
        int bytesRead;
        byte[] buffer = new byte[8192];
        while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
        ins.close();
        return file;
    }
}
