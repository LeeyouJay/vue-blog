package com.arslinth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Arslinth
 * @ClassName LoadImgConfig
 * @Description 对图片资源访问放行
 * @Date 2021/3/2
 */
@Configuration
public class LoadImgConfig implements WebMvcConfigurer {
    @Value("${file.staticAccessPath}")
    private String staticAccessPath;
    @Value("${file.uploadFolder}")
    private String uploadFolder;

    @Value("${file.musicStaticPath}")
    private String musicStaticPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(staticAccessPath).addResourceLocations("file:" + uploadFolder);
        registry.addResourceHandler(musicStaticPath).addResourceLocations("file:" + uploadFolder);
    }
}
