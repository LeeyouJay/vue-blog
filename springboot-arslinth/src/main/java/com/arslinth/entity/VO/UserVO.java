package com.arslinth.entity.VO;

import com.arslinth.entity.SysAuthority;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author Arslinth
 * @ClassName UserVO
 * @Description 与SysUser的区别在于有没有password属性
 * @Date 2021/2/26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {

    private String username;

    private String oldPassword;

    private String newPassword;

    private String[] sysAuthorities;

}
