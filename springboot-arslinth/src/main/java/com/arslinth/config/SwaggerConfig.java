package com.arslinth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Arslinth
 * @ClassName SwaggerConfig
 * @Description swagger2配置
 * @Date 2021/2/25
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket ProductApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(false)
                .pathMapping("/")
                //.enable(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.arslinth.controller"))
                .build()
                .apiInfo(productApiInfo());
    }

    private ApiInfo productApiInfo() {
        ApiInfo apiInfo = new ApiInfo("ArslinthBoot数据接口文档",
                "这是一个简单的接口文档",
                "1.0.0",
                "API TERMS URL",
                "752279593@qq.com",
                "license",
                "license url");
        return apiInfo;
    }

}
