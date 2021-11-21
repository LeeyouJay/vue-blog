package com.arslinth.dao;

import com.arslinth.entity.Comment;
import com.arslinth.entity.SysUser;
import com.arslinth.entity.VO.CommentVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author Arslinth
 * @Description TODO
 * @Date 2021/4/3
 */
@Repository
public interface CommentDao extends BaseMapper<Comment> {

    @Select("SELECT c.id,a.id AS articleId,a.title,c.from_user_name,c.from_user_email,c.create_time,c.has_new " +
            "FROM `comment` AS c " +
            "LEFT JOIN article AS a ON c.article_id = a.id "+
            "WHERE ISNULL(parent_id) AND a.user_id = #{userId} ORDER BY c.has_new DESC, c.create_time DESC")
    List<CommentVO> getCommentVO(String userId);

    @Update("UPDATE `comment` SET has_new = #{hasNew} WHERE id =#{id}")
    int updateNew(String id,boolean hasNew);


    @Select("SELECT u.nick_name ,u.email ,a.title FROM article AS a " +
            "LEFT JOIN sys_user AS u ON a.user_id = u.id " +
            "WHERE a.id = #{articleId} ")
    Map<String,String> findAuthor(String articleId);

    @Update("UPDATE `comment` SET from_user_avatar = #{avatar} WHERE from_user_id =#{userId}")
    int changeAvatar(String userId,String avatar);

}
