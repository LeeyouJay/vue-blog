package com.arslinth.service;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.druid.util.StringUtils;
import com.arslinth.dao.SysLogDao;
import com.arslinth.entity.VO.QueryBody;
import com.arslinth.entity.SysLog;
import com.arslinth.utils.IpInfoUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Arslinth
 * @ClassName SysLogService
 * @Description TODO
 * @Date 2021/3/4
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SysLogService {
    private final SysLogDao sysLogDao;

    public void saveSysLog(HttpServletRequest request,String type, String message,Boolean isSuccess,String username){
        try {
            // 获取ip地址
            String ipAddr = IpInfoUtils.getIpAddr(request);
            // 获取ip来源
            String ipSource = IpInfoUtils.getipSource(ipAddr);
            //获取浏览器信息
            String browser = IpInfoUtils.getBrowser(request);
            // 获取系统名称
            String systemName = IpInfoUtils.getSystemName(request);
            SysLog sysLog = SysLog.builder()
                    .username(username)
                    .browserName(browser)
                    .type(type)
                    .id(RandomUtil.randomString(16))
                    .ipAddress(ipAddr)
                    .ipSource(ipSource)
                    .message(message)
                    .isSuccess(isSuccess)
                    .systemName(systemName)
                    .build();
            sysLogDao.insert(sysLog);
        } catch (Exception e) {
            log.error("获取ip来源出错");
            e.printStackTrace();

        }
    }
    public void insertSysLog(SysLog sysLog,String type, String message,Boolean isSuccess,String username){
        sysLog.setId(RandomUtil.randomString(16));
        sysLog.setUsername(username);
        sysLog.setType(type);
        sysLog.setMessage(message);
        sysLog.setSuccess(isSuccess);
        sysLogDao.insert(sysLog);
    }

    public List<SysLog> getLogList(QueryBody query){
        QueryWrapper<SysLog> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(query.getSearchName())){
            wrapper.like("username",query.getSearchName())
            .or().like("ip_source",query.getSearchName());
        }
        if(!StringUtils.isEmpty(query.getState())){
            wrapper.eq("is_success",query.getState());
        }
        wrapper.orderByDesc("create_time");
        return sysLogDao.selectList(wrapper);
    }

    public SysLog getLatestLogByUser(String username){
        QueryWrapper<SysLog> wrapper = new QueryWrapper<>();
        wrapper.eq("username",username).orderByDesc("create_time");
        List<SysLog> sysLogs = sysLogDao.selectList(wrapper);
        if(sysLogs!= null && sysLogs.size()>1)
            return sysLogs.get(1);
        else
            return null;
    }

}
