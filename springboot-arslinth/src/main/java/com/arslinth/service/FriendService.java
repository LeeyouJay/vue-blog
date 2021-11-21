package com.arslinth.service;

import cn.hutool.core.util.RandomUtil;
import com.arslinth.dao.FriendDao;
import com.arslinth.entity.Friend;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final MailService mailService;

    public int applyFriend(Friend friend){
        friend.setId(RandomUtil.randomString(16));
        Map<String, String> map = new HashMap<>();
        map.put("siteName",friend.getSiteName());
        map.put("siteLink",friend.getSiteLink());
        map.put("email",friend.getEmail());
        map.put("icon",friend.getIcon());
        map.put("description",friend.getDescription());
        mailService.sendMail("752279593@qq.com","您的博客Arslinth有了新的友链申请~","friendApply",map);
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
    public Friend findBySite(String siteLink){
        return friendDao.selectOne(new QueryWrapper<Friend>().eq("site_link",siteLink));
    }
}
