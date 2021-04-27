package com.arslinth.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Arslinth
 * @ClassName Friend
 * @Description TODO
 * @Date 2021/4/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Friend {
    private String id;

    private String siteName;

    private String email;

    private String siteLink;

    private String icon;

    private String description;

    private boolean hasCheck;

    @TableField(exist = false)
    private String tempDesc;

    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    @TableField(fill = FieldFill.UPDATE)
    private String updateTime;

}
