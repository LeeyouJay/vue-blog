package com.arslinth.service;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.druid.util.StringUtils;
import com.arslinth.common.Constants;
import com.arslinth.dao.SysUserDao;
import com.arslinth.dao.UserAuthorityDao;
import com.arslinth.entity.VO.QueryBody;
import com.arslinth.entity.SysUser;
import com.arslinth.utils.AuthenticationUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author Arslinth
 * @ClassName SysUserService
 * @Description 用户相关服务
 * @Date 2021/2/22
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SysUserService{

    private final SysUserDao sysUserDao;

    private final UserAuthorityDao userAuthorityDao;


    @Value("${signUp.authorities}")
    private List<String> defaultAuthorities;

    @Value("${signUp.avatarUrl}")
    private String defaultAvatar;

    public void signUp(SysUser sysUser) {
        sysUser.setId(RandomUtil.randomString(16));
        sysUser.setPassword(new BCryptPasswordEncoder().encode(sysUser.getPassword()));
        sysUser.setState(true);
        sysUser.setRoleId("non");
        sysUser.setNickName(sysUser.getUsername());
        sysUser.setAvatar(defaultAvatar);
        userAuthorityDao.setUserAuthorities(sysUser.getUsername(),defaultAuthorities);
        sysUserDao.insert(sysUser);
    }

    public List<SysUser> getUserList(QueryBody query) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        if (!StringUtils.isEmpty(query.getState())){
            wrapper.eq("state",query.getState());
        }
        if(!StringUtils.isEmpty(query.getSearchName())){
            wrapper.like("nick_name", query.getSearchName())
                    .or().like("username", query.getSearchName())
                    .or().like("phone", query.getSearchName());
        }

        List<SysUser> sysUsers = sysUserDao.selectList(wrapper);
        sysUsers.forEach(u -> {
            List<String> authorities = userAuthorityDao.getUserAuthorities(u.getUsername());
            u.setPassword("");//把密码设为空
            u.setSysAuthorities(authorities.toArray(new String[authorities.size()]));
        });
        return sysUsers;
    }

    public SysUser findByName(String username) {
        return sysUserDao.selectOne(new QueryWrapper<SysUser>().eq("username", username));
    }

    public SysUser findByPhoneOrEmail(String phoneEmail) {
        return sysUserDao.selectOne(new QueryWrapper<SysUser>().eq("phone", phoneEmail).or().eq("email",phoneEmail));
    }
    public SysUser findByPhone(String phone){
        if (StringUtils.isEmpty(phone))
            return null;
        return sysUserDao.selectOne(new QueryWrapper<SysUser>().eq("phone", phone));
    }
    public SysUser findByEmail(String email){
        return sysUserDao.selectOne(new QueryWrapper<SysUser>().eq("email", email));
    }

    public int resetPassword(SysUser sysUser) {
        sysUser.setPassword(new BCryptPasswordEncoder().encode(Constants.RESET_CODE));
        return sysUserDao.updateById(sysUser);
    }
    public int setState(SysUser sysUser) {
        return sysUserDao.setState(sysUser);
    }

    public int changePassword(SysUser sysUser) {
        return sysUserDao.updateById(sysUser);
    }
    public int changeUserInfo(SysUser sysUser){
        SysUser user = findByEmail(sysUser.getEmail());
        String name = AuthenticationUtils.getAuthenticationName();
        if(user!=null && !user.getUsername().equals(name)){
           return -1;
        }
        user = findByPhone(sysUser.getPhone());
        if(user!=null && !user.getUsername().equals(name)){
            return -1;
        }
        return sysUserDao.changeUserInfo(sysUser);
    }
    public void setRight(String username){
        UpdateWrapper<SysUser> wrapper = new UpdateWrapper<SysUser>().eq("username", username).set("set_right", true);
        sysUserDao.update(null, wrapper);
    }


}
