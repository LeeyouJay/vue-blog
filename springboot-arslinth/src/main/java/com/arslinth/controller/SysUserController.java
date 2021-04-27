package com.arslinth.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReUtil;
import com.arslinth.common.ApiResponse;
import com.arslinth.common.Constants;
import com.arslinth.common.ResponseCode;
import com.arslinth.config.redis.RedisTool;
import com.arslinth.dao.SysAuthorityDao;
import com.arslinth.dao.UserAuthorityDao;
import com.arslinth.entity.VO.QueryBody;
import com.arslinth.entity.SysAuthority;
import com.arslinth.entity.SysUser;
import com.arslinth.entity.VO.UserVO;
import com.arslinth.service.MailService;
import com.arslinth.service.MenuService;
import com.arslinth.service.SysUserService;
import com.arslinth.service.UploadService;
import com.arslinth.utils.AuthenticationUtils;
import com.arslinth.utils.PageUtil;
import com.arslinth.utils.VerifyCodeUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @author Arslinth
 * @ClassName SysUserController
 * @Description 用户相关API
 * @Date 2021/2/22
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SysUserController {

    private final SysUserService sysUserService;

    private final UserAuthorityDao userAuthorityDao;

    private final SysAuthorityDao sysAuthorityDao;

    private final UploadService uploadService;

    private final MenuService menuService;

    private final MailService mailService;

    private final RedisTool redisTool;

    //用户注册
    @PostMapping("/signup")
    public ApiResponse signUp(@RequestBody SysUser sysUser){


        if(!redisTool.exists(sysUser.getEmail()+Constants.MOBILE_CODE_SUFFIX)){
            return  ApiResponse.code(ResponseCode.CHECK_CAPTCHA_FAIL).message("验证码已过期！");
        }

        String value = redisTool.get(sysUser.getEmail()+Constants.MOBILE_CODE_SUFFIX).toString();

        SysUser check = sysUserService.findByName(sysUser.getUsername());
        if (check!=null)
            return ApiResponse.code(ResponseCode.FAIL).message("登入账号已存在！");
        //验证码先放在了userId上，通过认证之后会重新设置新的id
        if (!value.equalsIgnoreCase(sysUser.getId())){
            return  ApiResponse.code(ResponseCode.CHECK_CAPTCHA_FAIL).message("验证码不正确！");
        }
        sysUserService.signUp(sysUser);
        return ApiResponse.code(ResponseCode.SUCCESS).message("注册成功！");
    }
    //获取注册验证码
    @GetMapping("/sendVerifyCode/{phoneEmail}")
    public ApiResponse sendCode(@PathVariable String phoneEmail){
        SysUser user = sysUserService.findByPhoneOrEmail(phoneEmail);
        if (user != null){
            return ApiResponse.code(ResponseCode.FAIL).message("邮箱绑定用户已存在！");
        }
        int code = RandomUtil.randomInt(1000,9999);
        redisTool.set(phoneEmail+ Constants.MOBILE_CODE_SUFFIX,code,2L,TimeUnit.MINUTES);
        HashMap<String, String> map = new HashMap<>();
        map.put("code",code+"");
        mailService.sendMail(phoneEmail,"登入验证","mailCode",map);
        log.info(" 验证码为：{}",code);
        return ApiResponse.code(ResponseCode.SUCCESS).message("邮箱验证码发送成功！");
    }

    //获取登入验证码
    @GetMapping("/getMobileCode/{phoneEmail}")
    public ApiResponse getMobileCode(@PathVariable String phoneEmail){
        //匹配是否为邮箱
        boolean match = ReUtil.isMatch("\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}", phoneEmail);
        SysUser user = sysUserService.findByPhoneOrEmail(phoneEmail);
        if (user == null){
            return ApiResponse.code(ResponseCode.FAIL).message("手机或邮箱绑定用户不存在！");
        }

        int code = RandomUtil.randomInt(1000,9999);
        redisTool.set(phoneEmail+ Constants.MOBILE_CODE_SUFFIX,code,2L,TimeUnit.MINUTES);
        if(match){
            HashMap<String, String> map = new HashMap<>();
            map.put("code",code+"");
            //mailService.sendMail(phoneEmail,"登入验证","mailCode",map);
            log.info(" 验证码为：{}",code);
        }else
            log.info("手机号 "+phoneEmail+" 验证码为：{}",code);
        return ApiResponse.code(ResponseCode.SUCCESS).message("验证码发送成功！");
    }


    //获取图形验证码
    @GetMapping("/captchaImage")
    public ApiResponse getCode() throws IOException{
        String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
        String uuid = IdUtil.simpleUUID();
        redisTool.set(uuid,verifyCode,2L, TimeUnit.MINUTES);
        int w = 111, h = 36;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        VerifyCodeUtils.outputImage(w, h, stream, verifyCode);
        try {
            Base64.Encoder encoder = Base64.getEncoder();
            return ApiResponse.code(ResponseCode.SUCCESS).data("uuid",uuid).data("img",encoder.encodeToString(stream.toByteArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.code(ResponseCode.FAIL).message("图片生成失败！");
        } finally {
            stream.close();
        }
    }


    //左侧菜单数据
    @GetMapping("/getMenuList")
    @PreAuthorize("hasAnyAuthority('dashboard')")
    public ApiResponse getMenuList(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        List<SysAuthority> list = menuService.getMenuList(username);

        List<SysAuthority> menuList = list.stream()
                .filter(menu -> menu.getChildren().size() > 0).collect(Collectors.toList());

        List<SysAuthority> singleMenu = list.stream()
                .filter(menu -> menu.getChildren().size() == 0).collect(Collectors.toList());
        return ApiResponse.code(ResponseCode.SUCCESS).message("数据请求成功！").data("menuList",menuList).data("singleMenu",singleMenu);
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('user_list')")
    public ApiResponse userList(@RequestBody QueryBody query){
        List<SysUser> userList = sysUserService.getUserList(query);
        return ApiResponse.code(ResponseCode.SUCCESS)
                .data("list", PageUtil.limit(userList, query.getPageIndex(), query.getPageSize()))
                .data("pageTotal",userList.size());
    }

    @GetMapping("/authorityList")
    @PreAuthorize("hasAnyAuthority('user_list')")
    public ApiResponse getAuthorities(){
        List<SysAuthority> list = sysAuthorityDao.selectList(new QueryWrapper<SysAuthority>().orderByAsc("level","sorted"));
        Map<String, List<SysAuthority>> map = list.stream().collect(Collectors.groupingBy(SysAuthority::getParentId));
        list = list.stream().filter(l->!"dashboard".equals(l.getAuthority())).map(item -> {
            item.setChildren(map.get(item.getId())==null?new ArrayList<>():map.get(item.getId()));
            return item;
        }).filter(each -> "none".equals(each.getParentId())).collect(Collectors.toList());

        return ApiResponse.code(ResponseCode.SUCCESS).message("数据请求成功！").data("authorityTree",list);
    }
    //设置用户权限
    @PostMapping("/setAuthority")
    @PreAuthorize("hasAnyAuthority('setAuthority')")
    public ApiResponse setAuthority(@RequestBody UserVO userVO){
        ArrayList< String> asList = new ArrayList<>(userVO.getSysAuthorities().length);
        Collections.addAll(asList, userVO.getSysAuthorities());
        asList.add("dashboard");
        sysUserService.setRight(userVO.getUsername());
        boolean b = userAuthorityDao.setUserAuthorities(userVO.getUsername(), asList);
        if (b)
            return ApiResponse.code(ResponseCode.SUCCESS).message("设置成功！");
        else
           return ApiResponse.code(ResponseCode.FAIL).message("更新失败！");
    }
    //重置密码
    @PostMapping("/resetPassword")
    @PreAuthorize("hasAnyAuthority('resetPassword')")
    public ApiResponse resetPassword(@RequestBody SysUser sysUser){
        int i = sysUserService.resetPassword(sysUser);
        if (i==1)
            return ApiResponse.code(ResponseCode.SUCCESS).message("重置成功！");
        else
            return ApiResponse.code(ResponseCode.FAIL).message("密码重置失败！");
    }

    @PostMapping("/setState")
    @PreAuthorize("hasAnyAuthority('changState')")
    public ApiResponse setState(@RequestBody SysUser sysUser){
        int i = sysUserService.setState(sysUser);
        if (i==1)
            return ApiResponse.code(ResponseCode.SUCCESS).message("更改成功！");
        else
            return ApiResponse.code(ResponseCode.FAIL).message("更改失败！");
    }
    @PostMapping("/changePassword")
    public ApiResponse changePassword(@RequestBody UserVO userVO){
        SysUser user = sysUserService.findByName(userVO.getUsername());

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (!passwordEncoder.matches(userVO.getOldPassword(),user.getPassword())){
            return ApiResponse.code(ResponseCode.FAIL).message("原密码不正确！");
        }
        user.setPassword(passwordEncoder.encode(userVO.getNewPassword()));

        int i = sysUserService.changePassword(user);
        if (i==1)
            return ApiResponse.code(ResponseCode.SUCCESS).message("密码更改成功！");
        else
            return ApiResponse.code(ResponseCode.FAIL).message("更改失败！");
    }

    @GetMapping("/getUserInfo")
    @PreAuthorize("hasAnyAuthority('dashboard')")
    public ApiResponse getUserInfo(){
        String username = AuthenticationUtils.getAuthenticationName();
        SysUser user = sysUserService.findByName(username);
        if (user != null)
            user.setPassword(null);
        return ApiResponse.code(ResponseCode.SUCCESS).message("数据请求成功！").data("userInfo",user);
    }

    @PostMapping("/changeUserInfo")
    @PreAuthorize("hasAnyAuthority('dashboard')")
    public ApiResponse changeUserInfo(@RequestBody SysUser sysUser){
        int i = sysUserService.changeUserInfo(sysUser);
        if (i==1)
            return ApiResponse.code(ResponseCode.SUCCESS).message("更改成功！");
        else if(i==-1)
            return ApiResponse.code(ResponseCode.FAIL).message("手机号或邮箱已被绑定！");
        else
            return ApiResponse.code(ResponseCode.FAIL).message("更改失败！");
    }
    @PostMapping("/changeAvatar")
    @PreAuthorize("hasAnyAuthority('dashboard')")
    public ApiResponse changeAvatar(@RequestParam("avatarfile") MultipartFile file){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        SysUser user = sysUserService.findByName(username);
        try {
            String imgUrl = uploadService.uploadImg(file, username);
            user.setAvatar(imgUrl);
            int i = sysUserService.changeUserInfo(user);
            if (i==1)
                return ApiResponse.code(ResponseCode.SUCCESS).message("更改成功！").data("imgUrl",imgUrl);
            else if(i==-1)
                return ApiResponse.code(ResponseCode.FAIL).message("手机号或邮箱已被绑定！");
            else
                return ApiResponse.code(ResponseCode.FAIL).message("更改失败！");
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.code(ResponseCode.FAIL).message("更改失败！");
        }
    }
}
