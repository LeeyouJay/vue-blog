package com.arslinth.controller;


import com.arslinth.common.ApiResponse;
import com.arslinth.common.ResponseCode;
import com.arslinth.dao.SysAuthorityDao;
import com.arslinth.entity.SysAuthority;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Arslinth
 * @ClassName IndexRestController
 * @Description 测试相关API
 * @Date 2021/2/22
 */

@RestController
@RequestMapping("/index")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IndexRestController {

    private final SysAuthorityDao sysAuthorityDao;

    @GetMapping("/xss")
    public String hello(String name){
        System.out.println(name);
        return name;
    }

    @GetMapping("/testApi")
    public ApiResponse testApi(){
        return ApiResponse.code(ResponseCode.SUCCESS).message("连接成功！");
    }
}
