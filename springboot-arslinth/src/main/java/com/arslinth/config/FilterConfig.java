package com.arslinth.config;

import com.arslinth.config.xss.XssFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Arslinth
 * @ClassName FilterConfig
 * @Description Filter过滤器配置
 * @Date 2021/3/26
 */
@Configuration
public class FilterConfig {

    @Value("${xss.excludes}")
    private String excludes;

    @Value("${xss.urlPatterns}")
    private String urlPatterns;

    @Value("${xss.isIncludeRichText}")
    private String isIncludeRichText;
    /**
     *Xss过滤器
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    public FilterRegistrationBean xssFilterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new XssFilter());
        filterRegistrationBean.setOrder(Integer.MAX_VALUE-1);
        filterRegistrationBean.setEnabled(true);
        if(urlPatterns.contains(",")){
            String[] split = StringUtils.split(urlPatterns, ",");
            filterRegistrationBean.addUrlPatterns(split);
        }else{
            filterRegistrationBean.addUrlPatterns(urlPatterns);
        }
        Map<String, String> initParameters = new HashMap<>(16);
        //excludes用于配置不需要参数过滤的请求url
        initParameters.put("excludes", excludes);
        //isIncludeRichText主要用于设置富文本内容是否需要过滤
        initParameters.put("isIncludeRichText", isIncludeRichText);
        filterRegistrationBean.setInitParameters(initParameters);
        return filterRegistrationBean;
    }
}
