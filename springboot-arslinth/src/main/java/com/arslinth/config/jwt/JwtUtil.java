package com.arslinth.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author Arslinth
 * @ClassName JwtUtil
 * @Description jwt工具类
 * @Date 2021/2/22
 */
@Slf4j
@Configuration
public class JwtUtil {
    /**
     * 两个常量
     *      EXPIRE:token过期时间
     *      APP_SECRET:秘钥
     */
    //已在配置文件设置两个参数
    public static long EXPIRE  ;
    public static String APP_SECRET ;


    @Value("${jwtUtil.expireTime}")
    public void setExpireTime(Long expireTime){
        JwtUtil.EXPIRE = expireTime*1000*60;
    }
    @Value("${jwtUtil.AppSecret}")
    public void setAppSecret(String AppSecret){
        JwtUtil.APP_SECRET = AppSecret;
    }

    //生成token字符串的方法
    public static String getJwtToken(String username,String phone, List<String> authorities){

        byte[] keyBytes = Decoders.BASE64.decode(APP_SECRET);
        Key key = Keys.hmacShaKeyFor(keyBytes);
        String JwtToken = Jwts.builder()
                //token的第一部分(JWT头):设置主题（声明信息）
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setSubject("JSON Web Token")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))//token的过期时间
                //token的第二部分(有效载荷):存储用户信息
                .claim("username",username)
                .claim("phone",phone)
                .claim("authorities",authorities)
                //token的第三部分(签名哈希):加密规则
                .signWith(key)
                //0.10jwt版本以上的方法原方法.signWith(SignatureAlgorithm.HS256, APP_SECRET)已过时
                .compact();
        return JwtToken;
    }

    /**
     * 判断token是否存在与有效
     * @param jwtToken
     * @return
     */
    public static boolean checkToken(String jwtToken) {
        if(StringUtils.isEmpty(jwtToken)) return false;
        try {
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            //e.printStackTrace();
            log.warn("令牌解析不通过,非有效token或已过期");
            log.warn(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 判断token是否存在与有效
     * @param request
     * @return
     */
    public static boolean checkToken(HttpServletRequest request) {
        try {
            String jwtToken = request.getHeader("Authorization");
            if(StringUtils.isEmpty(jwtToken)) return false;
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            //e.printStackTrace();
            log.warn("解析出错,非有效token");
            log.warn(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 根据token获取用户手机号
     * @param request
     * @return
     */
    public static String getUserPhonedByJwtToken(HttpServletRequest request) {
        String jwtToken = request.getHeader("Authorization");
        if(StringUtils.isEmpty(jwtToken)) return "";
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        Claims claims = claimsJws.getBody();
        return (String)claims.get("phone");
    }

    /**
     * 根据token获取用户名称
     * @param request
     * @return
     */
    public static String getUserNameByJwtToken(HttpServletRequest request) {
        String jwtToken = request.getHeader("Authorization");
        if(StringUtils.isEmpty(jwtToken)) return "";
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        Claims claims = claimsJws.getBody();
        return (String)claims.get("username");
    }
    public static List<String>  getAuthoritiesByJwtToken(HttpServletRequest request) {
        String jwtToken = request.getHeader("Authorization");
        if(StringUtils.isEmpty(jwtToken)) return new ArrayList<>();
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        Claims claims = claimsJws.getBody();
        return (List<String>)claims.get("authorities");
    }

    /**
     * 根据token获取claim
     * @param jwt
     * @return
     */
    public static Claims getClaims(String jwt){
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(APP_SECRET)
                    .parseClaimsJws(jwt).getBody();
            return claims;
        }catch (Exception exception){
            return null;
        }
    }
}
