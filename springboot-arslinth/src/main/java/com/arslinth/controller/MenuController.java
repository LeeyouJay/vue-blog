package com.arslinth.controller;

import com.arslinth.common.ApiResponse;
import com.arslinth.common.ResponseCode;
import com.arslinth.entity.SysAuthority;
import com.arslinth.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



/**
 * @author Arslinth
 * @ClassName MenuController
 * @Description 侧边菜单管理类
 * @Date 2021/2/27
 */
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MenuController {

    private final MenuService menuService;

    //修改按钮
    @GetMapping("/findById/{parentId}")
    @PreAuthorize("hasAnyAuthority('editMenu')")
    public ApiResponse findById(@PathVariable String parentId){
        return ApiResponse.code(ResponseCode.SUCCESS).message("数据请求成功！").data("parent",menuService.findById(parentId));
    }
    @PostMapping("/setMenu")
    @PreAuthorize("hasAnyAuthority('editMenu')")
    public ApiResponse setMenu(@RequestBody SysAuthority sysAuthority){
        int i = menuService.setMenu(sysAuthority);
        if (i == 1)
            return ApiResponse.code(ResponseCode.SUCCESS).message("修改成功！");
        else
            return ApiResponse.code(ResponseCode.FAIL).message("更新数据异常");

    }
    //添加按钮
    @PostMapping("/addMenu")
    @PreAuthorize("hasAnyAuthority('addMenu')")
    public ApiResponse addMenu(@RequestBody SysAuthority sysAuthority){
         menuService.addMenu(sysAuthority);
         return ApiResponse.code(ResponseCode.SUCCESS).message("添加成功！");
    }
    //删除菜单、按钮
    @GetMapping("/delMenu/{id}")
    @PreAuthorize("hasAnyAuthority('delMenu')")
    public ApiResponse delMenu(@PathVariable String id){
        int i = menuService.delMenuById(id);
        if (i == 1)
            return ApiResponse.code(ResponseCode.SUCCESS).message("删除成功！");
        else
            return ApiResponse.code(ResponseCode.FAIL).message("删除出现异常！");

    }
}
