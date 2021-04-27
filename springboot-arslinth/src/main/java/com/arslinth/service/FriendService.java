package com.arslinth.service;

import cn.hutool.core.util.RandomUtil;
import com.arslinth.dao.FriendDao;
import com.arslinth.entity.Friend;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Arslinth
 * @ClassName FriendService
 * @Description TODO
 * @Date 2021/4/21
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FriendService {
    private final FriendDao friendDao;


    public int applyFriend(Friend friend){
        friend.setId(RandomUtil.randomString(16));
        return friendDao.insert(friend);
    }
    public int passApply(Friend friend){
        return friendDao.updateById(friend);
    }
    public int delFriend(String id){
        return friendDao.deleteById(id);
    }
    public List<Friend> getFriends(){
        return friendDao.selectList(new QueryWrapper<Friend>().eq("has_check",true).orderByDesc("update_time"));
    }
    public List<Friend> getApplyFriends(){
        return friendDao.selectList(new QueryWrapper<Friend>().eq("has_check",false).orderByDesc("create_time"));
    }
}
