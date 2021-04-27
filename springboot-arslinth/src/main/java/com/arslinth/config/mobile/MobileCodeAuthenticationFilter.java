package com.arslinth.config.mobile;

import com.alibaba.fastjson.JSON;
import com.arslinth.common.ApiResponse;
import com.arslinth.common.Constants;
import com.arslinth.common.ResponseCode;
import com.arslinth.config.jwt.JwtUtil;
import com.arslinth.entity.SysUser;
import com.arslinth.exception.MyAccountException;
import com.arslinth.service.SysLogService;
import com.arslinth.utils.ResponseUtil;
import com.arslinth.utils.SysLogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Arslinth
 * @ClassName MobileCodeAuthenticationFilter
 * @Description 手机验证码登入过滤器
 * @Date 2021/3/5
 */
@Slf4j
public class MobileCodeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {


    private String mobileParameter = Constants.FORM_MOBILE_NUM;
    private String codeParameter = Constants.FORM_CODE;
    private boolean postOnly = true;

    private SysLogService sysLogService;

    public MobileCodeAuthenticationFilter(AuthenticationManager authManager,SysLogService sysLogService) {

        super(new AntPathRequestMatcher(Constants.MOBILE_LOGIN_URL, "POST"));
        this.sysLogService = sysLogService;
        this.setAuthenticationManager(authManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        String mobile = obtainMobile(request);
        String code = obtainCode(request);

        if (mobile == null) {
            mobile = "";
        }

        if (code == null) {
            code = "";
        }

        mobile = mobile.trim();
        code = code.trim();

        AbstractAuthenticationToken authRequest = new MobileCodeAuthenticationToken(mobile, code);

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected String obtainMobile(HttpServletRequest request) {
        return request.getParameter(mobileParameter);
    }

    protected String obtainCode(HttpServletRequest request) {
        return request.getParameter(codeParameter);
    }

    protected void setDetails(HttpServletRequest request,
                              AbstractAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authentication) throws IOException, ServletException {
        SysUser user = (SysUser) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> sysAuthorities = new ArrayList<>();
        authorities.forEach(item-> sysAuthorities.add(item.getAuthority()));
        String username = user.getUsername();
        String phone = user.getPhone();
        String jwtToken = JwtUtil.getJwtToken(username,phone,sysAuthorities);
        //返回并保存信息
        sysLogService.insertSysLog(SysLogUtils.getIpSource(request),"验证码方式","登录成功！",true,username);
        //sysLogService.saveSysLog(request,"验证码方式","登录成功！",true,username);

        ResponseUtil.print(response, JSON.toJSONString(ApiResponse.code(ResponseCode.SUCCESS)
                .message("登录成功！").data("token", jwtToken).data("nickName",user.getNickName()).data("avatar",user.getAvatar())));

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException e) throws IOException, ServletException {
        MyAccountException ex = (MyAccountException)e;
        String phone = obtainMobile(request);
        sysLogService.insertSysLog(SysLogUtils.getIpSource(request),"验证码方式",ex.getMessage(),false,phone);
        //sysLogService.saveSysLog(request,"验证码方式",ex.getMessage(),false,phone);
        log.warn(e.getMessage());
        ResponseUtil.print(response, JSON.toJSONString(ApiResponse.code(ex.getCode()).message(ex.getMessage())));
    }
}
