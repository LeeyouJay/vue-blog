package com.arslinth.dao;

import com.arslinth.common.Constants;
import com.arslinth.config.redis.RedisTool;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Arslinth
 * @ClassName ArticleTagDao
 * @Description TODO
 * @Date 2021/3/26
 */
@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ArticleTagDao {
    private final RedisTool redisTool;

    public List<String> getUserArticleTags(String userName) {
        if (redisTool.exists(userName + Constants.AUTHORITIES_SUFFIX))
            return (List<String>) redisTool.get(userName + Constants.ARTICLE_TAG_SUFFIX);
        else return new ArrayList<>();
    }

    public boolean setUserArticleTags(String username, List<String> tags) {
        return redisTool.set(username + Constants.ARTICLE_TAG_SUFFIX, tags);
    }
}
