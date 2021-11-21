package com.arslinth.controller;


import com.arslinth.common.ApiResponse;
import com.arslinth.common.ResponseCode;
import com.arslinth.dao.MottoDao;
import com.arslinth.dao.SysAuthorityDao;
import com.arslinth.entity.Motto;
import com.arslinth.entity.SysAuthority;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
@Slf4j
@RestController
@RequestMapping("/index")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IndexRestController {

    private final SysAuthorityDao sysAuthorityDao;

    private final MottoDao mottoDao;

    @GetMapping("/xss")
    public String hello(String name){
        System.out.println(name);
        return name;
    }

    @GetMapping("/testApi")
    public ApiResponse testApi(){
        return ApiResponse.code(ResponseCode.SUCCESS).message("连接成功！");
    }

    @PostMapping("/time")
    public ApiResponse testTime(@RequestBody Motto mottos){
        mottoDao.insert(mottos);
        return ApiResponse.code(ResponseCode.SUCCESS).message("插入成功！");
    }
}
