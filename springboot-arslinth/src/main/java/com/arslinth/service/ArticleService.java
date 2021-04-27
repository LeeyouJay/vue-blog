package com.arslinth.service;

import cn.hutool.core.util.RandomUtil;
import com.arslinth.dao.ArticleDao;
import com.arslinth.dao.SysUserDao;
import com.arslinth.entity.Article;
import com.arslinth.entity.SysUser;
import com.arslinth.entity.VO.ArticleQuery;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Arslinth
 * @ClassName ArticleService
 * @Description TODO
 * @Date 2021/3/19
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ArticleService {
    private final ArticleDao articleDao;

    private final SysUserDao sysUserDao;

    public int addArticle(Article article){
        article.setId(RandomUtil.randomString(16));
        return articleDao.insert(article);
    }
    public int editArticle(Article article){
        return articleDao.updateById(article);
    }
    public int delArticles(List<String> ids){
        return articleDao.deleteBatchIds(ids);
    }

    public List<Article> getArticleList(ArticleQuery articleQuery){
        List<Article> articles = articleDao.getArticleList(articleQuery);
        articles.stream().map(a->{
            if (a.getViewsCount()>500)
                a.setHot(true);
            return a;
        }).collect(Collectors.toList());
        return articles;
    }

    public Article getArticleById(String id){
        Article article = articleDao.selectById(id);
        if (article!=null){
            articleDao.viewCount(id);
            SysUser user = sysUserDao.selectById(article.getUserId());
            article.setAuthor(user.getNickName());
        }
        return article;
    }
    public List<String> getTags(){
        return articleDao.getTags();
    }

    public List<Article> getArticleStatistics(String userId){
        return articleDao.selectList(new QueryWrapper<Article>().eq("user_id",userId));
    }

}
