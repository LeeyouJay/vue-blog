package com.arslinth.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Arslinth
 * @ClassName Comment
 * @Description TODO
 * @Date 2021/4/3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private String id;

    private String articleId;

    private String fromUserId;

    private String fromUserName;

    private String fromUserAvatar;

    private String fromUserEmail;

    private String fromUserSite;

    private String toUserSite;

    private String toUserId;

    private String toUserEmail;

    private String toUserName;

    private String toUserAvatar;

    private String parentId;

    private String content;

    private String systemName;

    private String browser;

    private boolean hasNew;

    @TableField(exist = false)
    private String yourContent;

    @TableField(exist = false)
    private List<Comment> reply = new ArrayList<>();


    @TableField(fill = FieldFill.INSERT)
    private String createTime;



}
