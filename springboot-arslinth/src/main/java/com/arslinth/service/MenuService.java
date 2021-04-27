package com.arslinth.service;

import cn.hutool.core.util.RandomUtil;
import com.arslinth.dao.SysAuthorityDao;
import com.arslinth.dao.SysUserDao;
import com.arslinth.dao.UserAuthorityDao;
import com.arslinth.entity.SysAuthority;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Arslinth
 * @ClassName MenuService
 * @Description TODO
 * @Date 2021/2/27
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MenuService {

    private final SysAuthorityDao sysAuthorityDao;

    private final UserAuthorityDao userAuthorityDao;

    public List<SysAuthority> getMenuList(String username){

        List<String> auths = userAuthorityDao.getUserAuthorities(username);
        QueryWrapper<SysAuthority> wrapper = new QueryWrapper<>();
        wrapper
                .eq("authority_type", "页面")
                .in("authority",auths);
        List<SysAuthority> list = sysAuthorityDao.selectList(wrapper);
        Map<String, List<SysAuthority>> map = list.stream().collect(Collectors.groupingBy(SysAuthority::getParentId));
        list = list.stream().map(item -> {
            item.setChildren(map.get(item.getId())==null?new ArrayList<>():map.get(item.getId()));
            return item;
        }).filter(each -> "none".equals(each.getParentId())).sorted(
                Comparator.comparingInt(SysAuthority::getSorted)
        ).collect(Collectors.toList());
        return list;
    }

    public SysAuthority findById(String id){
        return sysAuthorityDao.selectById(id);
    }

    public int setMenu(SysAuthority sysAuthority){
        return sysAuthorityDao.updateById(sysAuthority);
    }

    public void addMenu(SysAuthority sysAuthority){
        sysAuthority.setId(RandomUtil.randomString(16));
        sysAuthorityDao.insert(sysAuthority);
    }
    public int delMenuById(String id){
        return sysAuthorityDao.deleteById(id);
    }



}
