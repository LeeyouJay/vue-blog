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
 * @ClassName UserAuthorityDao
 * @Description 用户权限层，使用NOSQL类型的数据库redis会更好操作，而不再将数据存入mysql中
 * @Date 2021/2/25
 */
@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserAuthorityDao {

    private final RedisTool redisTool;

    public List<String> getUserAuthorities(String userName) {
        if (redisTool.exists(userName + Constants.AUTHORITIES_SUFFIX))
            return (List<String>) redisTool.get(userName + Constants.AUTHORITIES_SUFFIX);
        else return new ArrayList<>();
    }

    public boolean setUserAuthorities(String username, List<String> authorities) {
        return redisTool.set(username + Constants.AUTHORITIES_SUFFIX, authorities);
    }
}
