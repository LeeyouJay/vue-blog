package com.arslinth.entity.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Arslinth
 * @ClassName CommentVO
 * @Description TODO
 * @Date 2021/4/19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentVO {
    private String id;

    private String articleId;

    private String title;

    private String fromUserName;

    private String fromUserEmail;

    private String CreateTime;

    private boolean hasNew;
}
