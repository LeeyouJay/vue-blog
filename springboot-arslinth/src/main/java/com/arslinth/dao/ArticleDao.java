package com.arslinth.dao;

import com.arslinth.entity.Article;
import com.arslinth.entity.VO.ArticleQuery;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author Arslinth
 * @Description TODO
 * @Date 2021/3/19
 */
@Repository
public interface ArticleDao extends BaseMapper<Article> {

    @Select("<script>" +
            "SELECT s.nick_name AS author, a.* FROM article a LEFT JOIN sys_user s ON s.id = a.user_id WHERE 1=1 " +
            "<when test=\" tag !='' and tag!=null\">" +
                "AND a.tag LIKE CONCAT('%',#{tag},'%') " +
            "</when>" +
            "<when test=\" author !='' and author!=null\">" +
                "AND s.username = #{author} " +
            "</when>" +
            "<when test=\" content !='' and content!=null\">" +
                "AND a.title LIKE CONCAT('%',#{content},'%') OR a.content LIKE CONCAT('%',#{content},'%') "+
            "</when>" +
            "ORDER BY a.is_top DESC ,a.create_time DESC "+
            "</script>")
    List<Article> getArticleList(ArticleQuery articleQuery);

    @Select("SELECT tag FROM article GROUP BY tag")
    List<String> getTags();

    //不触发update的自动填充
    @Select("UPDATE article SET views_count=views_count+1 WHERE id = #{id}")
    void viewCount(@Param("id") String id);

    @Select("UPDATE article SET comments_count=comments_count+#{num} WHERE id = #{id}")
    void commentCount(@Param("id") String id,int num);
}
