package com.arslinth.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Arslinth
 * @ClassName Article
 * @Description TODO
 * @Date 2021/3/19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    private String id;

    private String userId;

    @TableField(exist = false)
    private String author;

    private String title;

    private String banner;

    private String showType;

    private String content;

    private String summary;

    private String tag;

    private boolean isTop;

    @TableField(exist = false)
    private boolean isHot;

    private int viewsCount;

    private int commentsCount;

    private boolean markdown;

    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    @TableField(fill = FieldFill.UPDATE)
    private String updateTime;

}
