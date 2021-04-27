package com.arslinth.service;

import cn.hutool.core.util.RandomUtil;
import com.arslinth.dao.RoleAuthsDao;
import com.arslinth.dao.SysRoleDao;
import com.arslinth.dao.SysUserDao;
import com.arslinth.dao.UserAuthorityDao;
import com.arslinth.entity.SysRole;
import com.arslinth.entity.SysUser;
import com.arslinth.entity.VO.RoleAuths;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Arslinth
 * @ClassName SysRoleService
 * @Description TODO
 * @Date 2021/3/7
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SysRoleService {

    private final SysUserDao sysUserDao;

    private final SysRoleDao sysRoleDao;

    private final RoleAuthsDao roleAuthsDao;

    private final UserAuthorityDao userAuthorityDao;

    @Value("${signUp.authorities}")
    private List<String> defaultAuthorities;

    public List<SysRole> getRoles(){
        List<SysRole> sysRoles = sysRoleDao.selectList(new QueryWrapper<SysRole>().orderByAsc("create_time"));
        List<RoleAuths> roleAuths = roleAuthsDao.selectList(new QueryWrapper<>());
        HashMap<String,List<RoleAuths>> hashMap = roleAuths.stream().collect(Collectors.groupingBy(RoleAuths::getRoleId,HashMap::new ,Collectors.toList()));
        sysRoles.stream().map(sysRole -> {
            hashMap.forEach((key,value)->{
                if (sysRole.getId().equals(key)) {
                    sysRole.setAuthorities(value.stream().map(RoleAuths::getAuth).collect(Collectors.toList()));
                }
            });
            return sysRole;
        }).collect(Collectors.toList());
        return sysRoles;
    }

    public Map<String, List<SysUser>> getRoleUserList(){
        List<SysUser> sysUsers = sysUserDao.selectList(new QueryWrapper<>());
        Map<String, List<SysUser>> map = sysUsers.stream().map(sysUser->{
            sysUser.setPassword("");
            return sysUser;
        }).collect(Collectors.groupingBy(SysUser::getRoleId));

        return map;
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int setAuthorities(SysRole sysRole){
        String id = sysRole.getId();
        roleAuthsDao.delete(new QueryWrapper<RoleAuths>().eq("role_id",id).ne("auth","dashboard"));
        List<String> authorities = sysRole.getAuthorities();
        if(authorities == null || authorities.size()==0){
            authorities.add("dashboard");
        }
        List<RoleAuths> auths = new ArrayList<>();
        authorities.forEach(auth->{
            auths.add(RoleAuths.builder()
                    .roleId(id)
                    .auth(auth)
                    .build());
        });
        List<SysUser> userList = sysUserDao.selectList(new QueryWrapper<SysUser>().eq("role_id", id));
        userList.forEach(user->{
            userAuthorityDao.setUserAuthorities(user.getUsername(),authorities);
            if(user.isSetRight()){
                user.setSetRight(false);
                sysUserDao.updateById(user);
            }
        });
        sysRoleDao.updateById(sysRole);
        return roleAuthsDao.insertBatchSomeColumn(auths);
    }

    public int UserChangeRole(SysUser sysUser){
        UpdateWrapper<SysUser> wrapper = new UpdateWrapper<>();
        wrapper.eq("id",sysUser.getId()).set("role_id",sysUser.getRoleId()).set("set_right",false);
        List<RoleAuths> roleAuths = roleAuthsDao.selectList(new QueryWrapper<RoleAuths>().eq("role_id",sysUser.getRoleId()));
        List<String> auths = roleAuths.stream().map(RoleAuths::getAuth).collect(Collectors.toList());
        if(auths.size() == 0)
            userAuthorityDao.setUserAuthorities(sysUser.getUsername(),defaultAuthorities);
        else
            userAuthorityDao.setUserAuthorities(sysUser.getUsername(),auths);
        return sysUserDao.update(null,wrapper);
    }

    public int addSysRole(SysRole sysRole){
        sysRole.setId(RandomUtil.randomString(16));
        return sysRoleDao.insert(sysRole);
    }
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delRole(SysRole sysRole){
        String id = sysRole.getId();
        List<SysUser> userList = sysUserDao.selectList(new QueryWrapper<SysUser>().eq("role_id", id));
        userList.forEach(user->{
            user.setRoleId("non");
            user.setSetRight(true);
            sysUserDao.updateById(user);
        });
        return sysRoleDao.deleteById(id);
    }
}
