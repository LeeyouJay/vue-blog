package com.arslinth.entity.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Arslinth
 * @ClassName ArticleQuery
 * @Description TODO
 * @Date 2021/3/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleQuery {

    private String content;

    private String author;

    private String tag;

    private long pageIndex;

    private long pageSize;
}
