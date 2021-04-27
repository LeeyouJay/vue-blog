package com.arslinth.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Arslinth
 * @ClassName SysRole
 * @Description TODO
 * @Date 2021/3/7
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysRole {

    private String id;

    private String role;

    private String roleName;

    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    @TableField(exist = false)
    private List<String> authorities;

}
