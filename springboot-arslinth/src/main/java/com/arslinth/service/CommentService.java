package com.arslinth.service;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.arslinth.dao.ArticleDao;
import com.arslinth.dao.CommentDao;
import com.arslinth.dao.MottoDao;
import com.arslinth.entity.Comment;
import com.arslinth.entity.SysUser;
import com.arslinth.entity.VO.CommentVO;
import com.arslinth.utils.AuthenticationUtils;
import com.arslinth.utils.HtmlFilter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Arslinth
 * @ClassName CommentService
 * @Description TODO
 * @Date 2021/4/3
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommentService {

    private final CommentDao commentDao;

    private final ArticleDao articleDao;

    private final MailService mailService;

    private final MottoDao mottoDao;


    @Transactional
    public int addComment(Comment comment){
        comment.setId(RandomUtil.randomString(16));
        commentDao.updateNew(comment.getParentId(),true);
        HashMap<String, String> map = new HashMap<>();

        map.put("content", HtmlFilter.delHtmlTags(comment.getContent()));
        map.put("nickName",comment.getFromUserName());
        map.put("articleId",comment.getArticleId());
        map.put("motto",mottoDao.selectById(RandomUtil.randomInt(10)).getWords());

        if(!StringUtils.isEmpty(comment.getParentId())){
            map.put("toUserName",comment.getToUserName());
            map.put("yourContent", HtmlFilter.delHtmlTags(comment.getYourContent()));
            mailService.sendMail(comment.getToUserEmail(),"您在Arslinth的博客评论中有了新的回复呐~","reply",map);
        }else{
            Map<String,String> author = commentDao.findAuthor(comment.getArticleId());

            author = Optional.ofNullable(author).orElseGet(()->{
                Map<String, String> finalAuthor = new HashMap<>();
                finalAuthor.put("nick_name", "Arslinth");
                finalAuthor.put("title","留言");
                finalAuthor.put("email","752279593@qq.com");
                return finalAuthor;
            });

            map.put("toUserName",author.get("nick_name"));
            map.put("ArticleTile",author.get("title"));
            mailService.sendMail(author.get("email"),"您在Arslinth的博客中发表的文章《"+author.get("title")+"》有了新的留言呐~","ArticleMessage",map);
        }
        articleDao.commentCount(comment.getArticleId(),1);
        return commentDao.insert(comment);
    }

    public void changeNew(String id,boolean hasNew){
        commentDao.updateNew(id,hasNew);
    }

    public List<Comment> getComments(String articleId){
        List<Comment> comments = commentDao.selectList(new QueryWrapper<Comment>().eq("article_id", articleId).orderByDesc("create_time"));
        List<Comment> replies = comments.stream().filter(com -> com.getParentId() != null)
                .sorted(Comparator.comparing(Comment::getCreateTime))
                .collect(Collectors.toList());
        List<Comment> collect = comments.stream().filter(com -> com.getParentId() == null).map(com -> {
            List<Comment> list = new ArrayList<>();
            replies.forEach(r -> {
                if (r.getParentId().equals(com.getId()))
                    list.add(r);
            });
            com.setReply(list);
            return com;
        }).collect(Collectors.toList());

        return collect;
    }
    public String[] getQQInfo(Long qqId){
        StringBuilder jsonString = new StringBuilder();
        URLConnection connection ;
        try {
            URL urlObject = new URL("https://r.qzone.qq.com/fcg-bin/cgi_get_portrait.fcg?uins="+qqId);
            connection = urlObject.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"GBK"));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                jsonString.append(inputLine);
            }
            in.close();
            if (jsonString.indexOf("portraitCallBack(")!= -1){
                String substring = jsonString.substring(jsonString.indexOf("["), jsonString.length() - 2);
                JSONArray array = JSON.parseArray(substring);
                return new String[]{(String) array.get(0), (String) array.get(6)};
            }else
                return new String[]{"",""};
        }  catch (IOException e) {
            e.printStackTrace();
            return new String[]{"",""};
        }

    }

    public List<CommentVO> getCommentVO(String userId){
        return commentDao.getCommentVO(userId);
    }

    public void delComment(String id,String articleId){
        articleDao.commentCount(articleId,-1);
        commentDao.deleteById(id);
    }

}
