package com.arslinth.controller;

import com.arslinth.common.ApiResponse;
import com.arslinth.common.ResponseCode;
import com.arslinth.entity.VO.QueryBody;
import com.arslinth.entity.SysLog;
import com.arslinth.service.SysLogService;
import com.arslinth.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Arslinth
 * @ClassName SysLogController
 * @Description TODO
 * @Date 2021/3/4
 */
@RestController
@RequestMapping("/syslog")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SysLogController {

    private final SysLogService sysLogService;

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('sysLog')")
    public ApiResponse getLogList(@RequestBody QueryBody query){
        List<SysLog> list = sysLogService.getLogList(query);
        return ApiResponse.code(ResponseCode.SUCCESS).data("syslogList", PageUtil.limit(list,query.getPageIndex(),query.getPageSize()))
                .message("查询成功！")
                .data("pageTotal",list.size());
    }

    @GetMapping("getLatestLogByUser/{username}")
    @PreAuthorize("hasAnyAuthority('dashboard')")
    public ApiResponse getLatestLogByUser(@PathVariable String username){
        SysLog log = sysLogService.getLatestLogByUser(username);
        return ApiResponse.code(ResponseCode.SUCCESS).data("sysLog",log);
    }
}
