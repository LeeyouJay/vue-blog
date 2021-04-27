package com.arslinth.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Arslinth
 * @ClassName SysRole
 * @Description 权限实体类
 * @Date 2021/2/22
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SysAuthority {

    private String id ;

    private String parentId;

    private String authority;

    private String authorityName;

    private String authorityType;

    private int level;

    private int sorted;

    private String icon;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    @TableField(exist = false)
    private boolean disabled;

    @TableField(exist = false)
    private List<SysAuthority> children;
}
