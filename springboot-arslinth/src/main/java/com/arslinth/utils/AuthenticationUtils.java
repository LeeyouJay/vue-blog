package com.arslinth.utils;

import com.arslinth.dao.RoleAuthsDao;
import com.arslinth.dao.UserAuthorityDao;
import com.arslinth.entity.SysUser;
import com.arslinth.entity.VO.RoleAuths;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Arslinth
 * @ClassName AuthenticationUtils
 * @Description 登入账号判断工具
 * @Date 2021/4/3
 */

public class AuthenticationUtils {

    public static String getAuthenticationName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public static Boolean isLogin(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !authentication.getCredentials().equals("");
    }

    //登入设置权限
    public static UsernamePasswordAuthenticationToken setAuthorities(
            UserAuthorityDao userAuthorityDao, RoleAuthsDao roleAuthsDao, SysUser user, String password){
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        List<String> userAuthorities = userAuthorityDao.getUserAuthorities(user.getUsername());

        //在内存中没有权限时
        if(userAuthorities.size() == 0){
            List<RoleAuths> roleAuths = roleAuthsDao.selectList(new QueryWrapper<RoleAuths>().eq("role_id", user.getRoleId()));
            userAuthorities = roleAuths.stream().map(RoleAuths::getAuth).collect(Collectors.toList());
            userAuthorityDao.setUserAuthorities(user.getUsername(),userAuthorities);
        }

        for (String auth : userAuthorities) {
            if (StringUtils.isEmpty(auth)) continue;
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(auth);
            authorities.add(authority);
        }
        return new UsernamePasswordAuthenticationToken(user, password, authorities);

    }
}
