package com.arslinth.config.jwt;

import com.arslinth.common.Constants;
import com.arslinth.common.ResponseCode;
import com.arslinth.config.redis.RedisTool;
import com.arslinth.dao.RoleAuthsDao;
import com.arslinth.dao.UserAuthorityDao;
import com.arslinth.entity.SysUser;
import com.arslinth.entity.VO.RoleAuths;
import com.arslinth.exception.MyAccountException;
import com.arslinth.service.SysUserService;
import com.arslinth.utils.AuthenticationUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Arslinth
 * @ClassName MyAuthenticationProvider
 * @Description 自定义账号密码登入验证方式
 * @Date 2021/3/1
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MyAuthenticationProvider implements AuthenticationProvider {

    private final SysUserService sysUserService;

    private final UserAuthorityDao userAuthorityDao;

    private final RoleAuthsDao roleAuthsDao;

    private final RedisTool redisTool;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        validate(req);
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        String username = token.getName();
        String password = token.getCredentials().toString();
        SysUser user = sysUserService.findByName(username);
        if (user == null) {
            throw new MyAccountException(ResponseCode.NO_USER, "账号不存在！");
        }
        if (!user.isState()) {
            throw new MyAccountException(ResponseCode.IS_LOCKED, "账号已被禁用！");
        }
        if (redisTool.exists(username + Constants.FAIL_TIME_SUFFIX)) {
            if (redisTool.sGetSetSize(username + Constants.FAIL_TIME_SUFFIX) >= 5L) {
                throw new MyAccountException(ResponseCode.NO_PASS, "密码错误次数过多，请稍候尝试！");
            }
        }
        //判断密码
        if (!new BCryptPasswordEncoder().matches(password, user.getPassword())) {
            redisTool.sSetAndTime(username + Constants.FAIL_TIME_SUFFIX, 600L, new Date());//秒
            throw new MyAccountException(ResponseCode.NO_PASS, "密码不正确,还有" + (5 - redisTool.sGetSetSize(username + Constants.FAIL_TIME_SUFFIX)) + "次机会！");
        }
        redisTool.remove(user.getUsername() + Constants.FAIL_TIME_SUFFIX);
        UsernamePasswordAuthenticationToken authorities = AuthenticationUtils.setAuthorities(userAuthorityDao, roleAuthsDao, user, password);
        return authorities;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

    //验证图片
    private void validate(HttpServletRequest request) {
        int MOVE_CHECK_ERROR = 5;//误差5个像素
        String moveX = request.getParameter("moveX");
        String captchaUUid = request.getParameter("captchaUUid");
        if (com.alibaba.druid.util.StringUtils.isEmpty(moveX)) {
            throw new MyAccountException(ResponseCode.CHECK_CAPTCHA_FAIL, "验证的值不能为空！");
        }
        if (!redisTool.exists(captchaUUid)) {
            throw new MyAccountException(ResponseCode.CHECK_CAPTCHA_FAIL, "验证已过期,请刷新页面！");
        }
        String codeVal = redisTool.get(captchaUUid).toString();
        int value = Integer.valueOf(codeVal);
        int X = Integer.valueOf(moveX);

        if (!((X < (value + MOVE_CHECK_ERROR))
                && (X > (value - MOVE_CHECK_ERROR)))) {
            throw new MyAccountException(ResponseCode.CHECK_CAPTCHA_FAIL, "验证失败！");
        }
        redisTool.remove(captchaUUid);
    }
}
