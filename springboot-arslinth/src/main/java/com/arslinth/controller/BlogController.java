package com.arslinth.controller;


import com.arslinth.common.ApiResponse;
import com.arslinth.common.ResponseCode;
import com.arslinth.entity.Article;
import com.arslinth.entity.Comment;
import com.arslinth.entity.Friend;
import com.arslinth.entity.SysUser;
import com.arslinth.entity.VO.ArticleQuery;
import com.arslinth.entity.VO.QueryBody;
import com.arslinth.service.*;
import com.arslinth.utils.AuthenticationUtils;
import com.arslinth.utils.IpInfoUtils;
import com.arslinth.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Arslinth
 * @ClassName BlogController
 * @Description TODO
 * @Date 2021/4/3
 */
@RestController
@RequestMapping("/blog")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BlogController {

    private final ArticleService articleService;

    private final SysUserService sysUserService;

    private final CommentService commentService;

    private final FriendService friendService;

    private final SongService songService;

    @GetMapping("/getArchives")
    public ApiResponse getArchives(){
        String username = AuthenticationUtils.getAuthenticationName();
        Boolean isLog = AuthenticationUtils.isLogin();
        List<Article> articleList = articleService.getArticleList(new ArticleQuery());
        int resCode;
        if(isLog){
            SysUser user = sysUserService.findByName(username);
            articleList= articleList.stream()
                    .filter(item->item.getShowType().equals("公开")||item.getUserId().equals(user.getId()))
                    .collect(Collectors.toList());
            resCode = ResponseCode.SUCCESS;
        }else{
            articleList= articleList.stream()
                    .filter(item->item.getShowType().equals("公开"))
                    .collect(Collectors.toList());
            resCode = ResponseCode.NO_LOGIN;
        }
        Map<String, List<Article>> map = articleList.stream().collect(
                Collectors.groupingBy(item -> item.getCreateTime().substring(0, 7)));
        return ApiResponse.code(resCode).data("archives",map);
    }


    @GetMapping("/article/{id}")
    public ApiResponse getArticle(@PathVariable String id){
        Boolean aBoolean = AuthenticationUtils.isLogin();
        int resCode;
        if (aBoolean)
            resCode = ResponseCode.SUCCESS;
        else
            resCode = ResponseCode.NO_LOGIN;
        Article article = articleService.getArticleById(id);
        return ApiResponse.code(resCode).data("article",article);
    }

    @GetMapping("/tags")
    public ApiResponse getTags(){
        return ApiResponse.code(ResponseCode.SUCCESS).data("tags",articleService.getTags());
    }

    @PostMapping("/addComment")
    public ApiResponse addComment(@RequestBody Comment comment, HttpServletRequest request){
        Boolean aBoolean = AuthenticationUtils.isLogin();
        if (aBoolean){
            String username = AuthenticationUtils.getAuthenticationName();
            SysUser user = sysUserService.findByName(username);
            comment.setFromUserAvatar(user.getAvatar());
            comment.setFromUserId(user.getId());
            comment.setFromUserName(user.getNickName());
            comment.setFromUserEmail(user.getEmail());
        }
        if(StringUtils.isEmpty(comment.getFromUserName())&&StringUtils.isEmpty(comment.getFromUserEmail()))
            return ApiResponse.code(ResponseCode.NO_LOGIN).message("登入信息失效！");
        Friend friend = friendService.findBySite(comment.getFromUserSite());

        Optional.of(friend).map(Friend::getIcon).ifPresent(comment::setFromUserAvatar);

        comment.setBrowser(IpInfoUtils.getBrowser(request));
        comment.setSystemName(IpInfoUtils.getSystemName(request));
        int i = commentService.addComment(comment);
        if (i==1){
            return ApiResponse.code(ResponseCode.SUCCESS).message("提交成功！");
        }else
            return ApiResponse.code(ResponseCode.FAIL).message("错误代码："+i);
    }

    @PostMapping("/getComments/{id}")
    public ApiResponse getComments(@PathVariable String id , @RequestBody QueryBody queryBody){
        List<Comment> comments = commentService.getComments(id);
        if (!StringUtils.isEmpty(queryBody.getSearchName())){
            commentService.changeNew(queryBody.getSearchName(),false);
            return ApiResponse.code(ResponseCode.SUCCESS).data("comments",
                    comments.stream().filter(c->c.getId().equals(queryBody.getSearchName())));
        }
        return ApiResponse.code(ResponseCode.SUCCESS)
                .data("comments", PageUtil.limit(comments,queryBody.getPageIndex(),queryBody.getPageSize()))
                .data("pageTotal",comments.size());
    }

    @GetMapping("/getQQInfo/{qq}")
    public ApiResponse getQQInfo(@PathVariable Long qq){
        String[] info = commentService.getQQInfo(qq);
        return ApiResponse.code(ResponseCode.SUCCESS).data("nickName",info[1]).data("avatar",info[0]);
    }

    @PostMapping("/applyFriend")
    public ApiResponse applyFriend(@RequestBody Friend friend){
        int i = friendService.applyFriend(friend);
        if (i==1)
            return ApiResponse.code(ResponseCode.SUCCESS).message("提交成功！");
        else
            return ApiResponse.code(ResponseCode.FAIL).message("错误代码："+i);
    }
    @GetMapping("/getFriends")
    public ApiResponse getFriends(){
        List<Friend> friends = friendService.getFriends();
        return ApiResponse.code(ResponseCode.SUCCESS).data("friends",friends);

    }
    @GetMapping("/ArticleSong/{id}")
    public ApiResponse ArticleSong(@PathVariable String id){
        return ApiResponse.code(ResponseCode.SUCCESS).data("song",songService.findById(id));

    }

}
