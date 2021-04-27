package com.arslinth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.arslinth.common.ApiResponse;
import com.arslinth.common.ResponseCode;
import com.arslinth.entity.SysRole;
import com.arslinth.entity.SysUser;
import com.arslinth.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author Arslinth
 * @ClassName SysRoleController
 * @Description
 * @Date 2021/3/7
 */
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('sysRoles')")
    public ApiResponse getRoleList(){
        return ApiResponse.code(ResponseCode.SUCCESS).data("list",sysRoleService.getRoles()).message("数据获取成功！");
    }

    @PostMapping("/setAuthorities")
    @PreAuthorize("hasAnyAuthority('set_roleAuths')")
    public ApiResponse setAuthorities(@RequestBody SysRole sysRole){
        int i = sysRoleService.setAuthorities(sysRole);
        if(i == sysRole.getAuthorities().size() || i == 1)
            return  ApiResponse.code(ResponseCode.SUCCESS).message("设置成功！");
        else
            return ApiResponse.code(ResponseCode.FAIL).message("错误代码："+i);
    }
    @GetMapping("/UserList")
    @PreAuthorize("hasAnyAuthority('sysRoles')")
    public ApiResponse getRoleUserList(){
        Map<String, List<SysUser>> map = sysRoleService.getRoleUserList();
        JSONObject result = JSON.parseObject(JSON.toJSONString(map));
        return ApiResponse.code(ResponseCode.SUCCESS).data(result).message("数据获取成功！");
    }
    @PostMapping("/UserChangeRole")
    @PreAuthorize("hasAnyAuthority('change_userAuths')")
    public ApiResponse UserChangeRole(@RequestBody SysUser sysUser){
        int i = sysRoleService.UserChangeRole(sysUser);
        if (i==1)
            return ApiResponse.code(ResponseCode.SUCCESS).message("变更成功！");
        else
            return ApiResponse.code(ResponseCode.FAIL).message("错误代码："+i);
    }
    @PostMapping("/addSysRole")
    @PreAuthorize("hasAnyAuthority('add_sysRole')")
    public ApiResponse addSysRole(@RequestBody SysRole sysRole){
        int i = sysRoleService.addSysRole(sysRole);
        if (i==1)
            return ApiResponse.code(ResponseCode.SUCCESS).message("添加成功！");
        else
            return ApiResponse.code(ResponseCode.FAIL).message("错误代码："+i);
    }
    @PostMapping("/delRole")
    @PreAuthorize("hasAnyAuthority('delete_sysRole')")
    public ApiResponse delRole(@RequestBody SysRole sysRole){
        int i = sysRoleService.delRole(sysRole);
        if (i==1)
            return ApiResponse.code(ResponseCode.SUCCESS).message("删除成功！");
        else
            return ApiResponse.code(ResponseCode.FAIL).message("错误代码："+i);
    }

}
