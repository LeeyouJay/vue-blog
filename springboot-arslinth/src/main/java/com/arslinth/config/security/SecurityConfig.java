package com.arslinth.config.security;

import com.arslinth.config.handler.JwtAuthorizedEntryPoint;
import com.arslinth.config.handler.JwtLogoutHandler;
import com.arslinth.config.jwt.JwtAuthenticationFilter;
import com.arslinth.config.jwt.JwtLoginAuthenticationFilter;
import com.arslinth.config.jwt.MyAuthenticationProvider;
import com.arslinth.config.mobile.MobileCodeAuthenticationFilter;
import com.arslinth.config.mobile.MobileCodeAuthenticationProvider;
import com.arslinth.config.redis.RedisTool;
import com.arslinth.service.SysLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

/**
 * @author Arslinth
 * @ClassName SecurityConfig
 * @Description security配置
 * @Date 2021/2/22
 */
// 开启 Security
// 开启注解配置支持
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final RedisTool redisTool;

    private final SysLogService sysLogService;

    private final MyAuthenticationProvider myAuthenticationProvider;

    private final MobileCodeAuthenticationProvider mobileCodeAuthenticationProvider;

    private final JwtAuthorizedEntryPoint jwtAuthorizedEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(myAuthenticationProvider);
        auth.authenticationProvider(mobileCodeAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.cors().and().csrf().disable();//关闭了csrf拦截的过滤器
        http.
                authorizeRequests().

                //所有请求都需要被认证
                //anyRequest().authenticated().

                //默认所有请求通过,并做了跨域请求处理
                requestMatchers(CorsUtils::isPreFlightRequest).permitAll().

                and().logout().logoutUrl("/logout").addLogoutHandler(new JwtLogoutHandler()).
                //未登入和未授权时的处理
                and().exceptionHandling().authenticationEntryPoint(jwtAuthorizedEntryPoint).
                //关闭session  用token验证，所以关闭session
                and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).
                //不能用自动装配方式，因为authenticationManager不能自动装配
                //登录过滤器，同时成功后创建token
                and().addFilter(new JwtLoginAuthenticationFilter(this.authenticationManager(),sysLogService))
                //该过滤器因为没有注入到spring容器中，所以创建一个构造方法，在配置中将redisTool传入该过滤器中
                .addFilter(new JwtAuthenticationFilter(this.authenticationManager(), redisTool))
                .addFilterBefore(new MobileCodeAuthenticationFilter(this.authenticationManager(),sysLogService), UsernamePasswordAuthenticationFilter.class);
    }

}
