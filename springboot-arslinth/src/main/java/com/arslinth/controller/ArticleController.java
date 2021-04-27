package com.arslinth.controller;

import cn.hutool.core.util.RandomUtil;
import com.arslinth.common.ApiResponse;
import com.arslinth.common.ResponseCode;
import com.arslinth.dao.ArticleTagDao;
import com.arslinth.entity.Article;
import com.arslinth.entity.SysUser;
import com.arslinth.entity.VO.ArticleQuery;
import com.arslinth.entity.VO.CommentVO;
import com.arslinth.entity.VO.QueryBody;
import com.arslinth.service.ArticleService;
import com.arslinth.service.CommentService;
import com.arslinth.service.SysUserService;
import com.arslinth.service.UploadService;
import com.arslinth.utils.AuthenticationUtils;
import com.arslinth.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Arslinth
 * @ClassName ArticleConctroller
 * @Description TODO
 * @Date 2021/3/19
 */
@RestController
@RequestMapping("/article")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ArticleController {
    private final ArticleService articleService;

    private final SysUserService sysUserService;

    private final UploadService uploadService;

    private final CommentService commentService;

    private final ArticleTagDao articleTagDao;
    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ht_publish','md_publish')")
    public ApiResponse addArticle(@RequestBody Article article){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        SysUser user = sysUserService.findByName(username);
        article.setUserId(user.getId());
        int i = articleService.addArticle(article);
        if (i == 1)
            return ApiResponse.code(ResponseCode.SUCCESS).message("添加成功！");
        else
            return ApiResponse.code(ResponseCode.FAIL).message("错误代码："+i);
    }
    @PostMapping("/edit")
    @PreAuthorize("hasAnyAuthority('ht_publish','md_publish')")
    public ApiResponse editArticle(@RequestBody Article article){
        int i = articleService.editArticle(article);
        if (i==1)
            return ApiResponse.code(ResponseCode.SUCCESS).message("修改成功！");
        else
            return ApiResponse.code(ResponseCode.FAIL).message("错误代码："+i);
    }
    @PostMapping("/del")
    @PreAuthorize("hasAnyAuthority('article_del')")
    public ApiResponse delArticles(@RequestBody List<String> ids){
        int i = articleService.delArticles(ids);
        if(i>0)
            return ApiResponse.code(ResponseCode.SUCCESS).message("删除成功！");
        else
            return ApiResponse.code(ResponseCode.FAIL).message("错误代码："+i);
    }

    @PostMapping("/imgAdd")
    @PreAuthorize("hasAnyAuthority('ht_publish','md_publish')")
    public ApiResponse imgAdd(@RequestParam("files")MultipartFile[] file){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString();
        try {
            List<String> imgUrlList = new ArrayList<>();
            for (int i = 0; i < file.length; i++) {
                String imgUrl = uploadService.uploadImg(file[i], username+RandomUtil.randomString(5));
                imgUrlList.add(imgUrl);
            }
            return ApiResponse.code(ResponseCode.SUCCESS).data("url",imgUrlList);
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.code(ResponseCode.FAIL).message("添加失败！");
        }
    }
    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('article_list')")
    public ApiResponse getArticleList(@RequestBody ArticleQuery articleQuery){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        articleQuery.setAuthor(username);
        List<Article> articleList = articleService.getArticleList(articleQuery);
        List<Article> limit = PageUtil.limit(articleList, articleQuery.getPageIndex(), articleQuery.getPageSize());
        return ApiResponse.code(ResponseCode.SUCCESS).data("articleList",limit)
                .data("pageTotal",articleList.size());
    }


    @PostMapping("/publicList")
    public ApiResponse getArticlePublicList(@RequestBody ArticleQuery articleQuery){
        String username = AuthenticationUtils.getAuthenticationName();
        Boolean isLog = AuthenticationUtils.isLogin();
        List<Article> articleList = articleService.getArticleList(articleQuery);
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

        List<Article> limit = PageUtil.limit(articleList, articleQuery.getPageIndex(), articleQuery.getPageSize());
        return ApiResponse.code(resCode).data("articleList",limit)
                .data("pageTotal",articleList.size());

    }

    @PostMapping("/saveTags")
    @PreAuthorize("hasAnyAuthority('article_list')")
    public ApiResponse saveTags(@RequestBody List<String> tagList){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        articleTagDao.setUserArticleTags(username, tagList);
        return ApiResponse.code(ResponseCode.SUCCESS).message("修改成功！");
    }
    @GetMapping("/getPersonalTags")
    @PreAuthorize("hasAnyAuthority('article_list')")
    public ApiResponse getPersonalTags(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        List<String> tags = articleTagDao.getUserArticleTags(username);
        return ApiResponse.code(ResponseCode.SUCCESS).data("tags",tags);
    }
    @PostMapping("/getCommentVO/{userId}")
    @PreAuthorize("hasAnyAuthority('dashboard')")
    public ApiResponse getCommentVO(@PathVariable String userId,@RequestBody QueryBody queryBody){
        List<CommentVO> list = commentService.getCommentVO(userId);
        return ApiResponse.code(ResponseCode.SUCCESS)
                .data("commentList",PageUtil.limit(list,queryBody.getPageIndex(),queryBody.getPageSize()))
                .data("pageTotal",list.size());
    }

    @GetMapping("/delComment/{id}/{articleId}")
    @PreAuthorize("hasAnyAuthority('dashboard')")
    public ApiResponse delComment(@PathVariable String id,@PathVariable String articleId){
        commentService.delComment(id,articleId);
        return ApiResponse.code(ResponseCode.SUCCESS).message("删除评论成功！");

    }
    @GetMapping("/statistics/{userId}")
    @PreAuthorize("hasAnyAuthority('dashboard')")
    public ApiResponse statistics(@PathVariable String userId){
        List<Article> list = articleService.getArticleStatistics(userId);

        int viewCount = list.stream().mapToInt(Article::getViewsCount).sum();
        int commentsCount = list.stream().mapToInt(Article::getCommentsCount).sum();
        Map<String, List<Article>> map = list.stream().collect(Collectors.groupingBy(Article::getTag));
        List<Map<String,String>> tags = new ArrayList<>();
        map.forEach((k,v)->{
            Map<String, String> data = new HashMap<>();
            data.put("tag",k);
            data.put("count",String.valueOf(v.size()));
            tags.add(data);
        });
        return ApiResponse.code(ResponseCode.SUCCESS).data("viewCount",viewCount)
                .data("commentsCount",commentsCount).data("tags",tags).data("articleCount",list.size());

    }

}
