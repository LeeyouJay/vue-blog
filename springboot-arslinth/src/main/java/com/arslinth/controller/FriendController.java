package com.arslinth.controller;

import com.arslinth.common.ApiResponse;
import com.arslinth.common.ResponseCode;
import com.arslinth.entity.Friend;
import com.arslinth.entity.VO.QueryBody;
import com.arslinth.service.FriendService;
import com.arslinth.service.MailService;
import com.arslinth.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

/**
 * @author Arslinth
 * @ClassName FriendController
 * @Description TODO
 * @Date 2021/4/22
 */
@RestController
@RequestMapping("/friend")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FriendController {

    private final FriendService friendService;

    private final MailService mailService;

    @PostMapping("/getFriends")
    @PreAuthorize("hasAnyAuthority('friend_list')")
    public ApiResponse applyFriend(@RequestBody QueryBody queryBody){
        List<Friend> applyFriends = friendService.getApplyFriends();
        List<Friend> friends = friendService.getFriends();
        return ApiResponse.code(ResponseCode.SUCCESS)
                .data("applyFriends", PageUtil.limit(applyFriends,queryBody.getPageIndex(),queryBody.getPageSize()))
                .data("applyTotal",applyFriends.size())
                .data("friends",PageUtil.limit(friends,queryBody.getPageIndex(),queryBody.getPageSize()))
                .data("friendsTotal",friends.size());

    }
    @PostMapping("/passApply")
    @PreAuthorize("hasAnyAuthority('friend_list')")
    public ApiResponse passApply(@RequestBody Friend friend){
        int i = friendService.passApply(friend);
        if (i==1){
            HashMap<String, String> map = new HashMap<>();
            if(!StringUtils.isEmpty(friend.getEmail()))
                mailService.sendMail(friend.getEmail(),"友链加入通知","applyPass",map);
            return ApiResponse.code(ResponseCode.SUCCESS).message("添加成功！");
        }else
            return ApiResponse.code(ResponseCode.FAIL).message("错误代码："+i);

    }
    @PostMapping("/delFriend")
    @PreAuthorize("hasAnyAuthority('friend_list')")
    public ApiResponse delFriend(@RequestBody Friend friend){
        if(!friend.isHasCheck()){
            HashMap<String, String> map = new HashMap<>();
            map.put("icon",friend.getIcon());
            map.put("siteName",friend.getSiteName());
            map.put("siteLink",friend.getSiteLink());
            map.put("email",friend.getEmail());
            map.put("content",friend.getTempDesc());
            map.put("description",friend.getDescription());
            map.put("createTime",friend.getCreateTime());
            if(!StringUtils.isEmpty(friend.getEmail()))
                mailService.sendMail(friend.getEmail(),"友链申请回复","applyNotPass",map);
        }
        int i = friendService.delFriend(friend.getId());
        if (i==1){
            return ApiResponse.code(ResponseCode.SUCCESS).message("删除成功！");
        }else{
            //发送邮件
            return ApiResponse.code(ResponseCode.FAIL).message("错误代码："+i);
        }
    }

}
