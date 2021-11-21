package com.arslinth.config.mobile;

import com.arslinth.common.Constants;
import com.arslinth.common.ResponseCode;
import com.arslinth.config.redis.RedisTool;
import com.arslinth.dao.UserAuthorityDao;
import com.arslinth.entity.SysUser;
import com.arslinth.exception.MyAccountException;
import com.arslinth.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author Arslinth
 * @ClassName MobileCodeAuthenticationProvider
 * @Description 自定义手机号登入验证
 * @Date 2021/3/5
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MobileCodeAuthenticationProvider implements AuthenticationProvider {

    private final SysUserService sysUserService;

    private final UserAuthorityDao userAuthorityDao;

    private final RedisTool redisTool;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String mobile = (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();
        String code = (String) authentication.getCredentials();

        SysUser user = sysUserService.findByPhoneOrEmail(mobile);

        if (user==null) {
            throw new MyAccountException(ResponseCode.NO_USER,"邮箱或手机号关联账号不存在！");
        }
        if (!user.isState()) {
            throw new MyAccountException(ResponseCode.IS_LOCKED,"账号已被禁用！");
        }

        if(!redisTool.exists(mobile+ Constants.MOBILE_CODE_SUFFIX)){
            throw new MyAccountException(ResponseCode.CHECK_CAPTCHA_FAIL,"验证码已过期！");
        }
        String codeValue = redisTool.get(mobile + Constants.MOBILE_CODE_SUFFIX).toString();

        if (!codeValue.equals(code)) {
            throw new MyAccountException(ResponseCode.CHECK_CAPTCHA_FAIL,"验证码不正确！");
        }
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        String username = user.getUsername();
        List<String> userAuthorities = userAuthorityDao.getUserAuthorities(username);
        for(String auth : userAuthorities) {
            if(StringUtils.isEmpty(auth)) continue;
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(auth);
            authorities.add(authority);
        }
        redisTool.remove(mobile + Constants.MOBILE_CODE_SUFFIX);
        return new MobileCodeAuthenticationToken(user,mobile,authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (MobileCodeAuthenticationToken.class.isAssignableFrom(authentication));
    }

}
