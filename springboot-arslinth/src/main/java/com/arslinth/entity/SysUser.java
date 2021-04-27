package com.arslinth.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Arslinth
 * @ClassName SysUser
 * @Description 用户信息实体类，继承UserDetails已实现序列化
 * @Date 2021/2/22
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SysUser {

    private String username;
    private String password;

    private String id;

    private String nickName;

    private String roleId;

    private String sex;

    private String phone;

    private String email;

    private String avatar;

    private boolean state;

    private boolean setRight;

    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    @TableField(exist = false)
    private String[] sysAuthorities;

}
