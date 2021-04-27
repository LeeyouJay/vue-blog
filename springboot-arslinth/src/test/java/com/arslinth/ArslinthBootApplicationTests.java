package com.arslinth;

import com.arslinth.config.redis.RedisTool;
import com.arslinth.dao.ArticleDao;
import com.arslinth.dao.CommentDao;
import com.arslinth.dao.SysAuthorityDao;
import com.arslinth.dao.SysUserDao;
import com.arslinth.entity.Article;
import com.arslinth.entity.Comment;
import com.arslinth.entity.SysUser;
import com.arslinth.entity.VO.ArticleQuery;
import com.arslinth.entity.VO.CommentVO;
import com.arslinth.service.ArticleService;
import com.arslinth.service.MailService;
import com.arslinth.utils.HtmlFilter;
import com.arslinth.utils.PageUtil;
import com.arslinth.utils.ThumbnailsUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ArslinthBootApplicationTests {

    private final RedisTool redisTool;

    private final SysUserDao sysUserDao;

    private final SysAuthorityDao sysAuthorityDao;

    private final ArticleDao articleDao;

    private final ArticleService articleService;

    private final MailService mailService;

    private final CommentDao commentDao;

    @Value("${xss.urlPatterns}")
    private String urlPatterns;

    @Value("${jwtUtil.expireTime}")
    private Long expireTime;

    @Value("${signUp.authorities}")
    private List<String> test;


    @Value("${file.uploadFolder}")
    private String realBasePath;

    @Test
    void contextLoads() {
        try {
//            String abc = "C://Users//Arslinth//Pictures//spriingImg//";
//            String strings = ThumbnailsUtil.generateThumbnail2Directory(abc + "csss.jpg");
//            System.out.println(strings);
            int i =0;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    void show() {
//        List<SysUser> sysUsers = sysUserDao.selectList(new QueryWrapper<>());
//        Map<String, List<SysUser>> map = sysUsers.stream().collect(Collectors.groupingBy(SysUser::getRoleId));
//        System.out.println(map);
    }

    @Test
    void daoTest(){
//        HashMap<String, String> map = new HashMap<>();
//
//        map.put("siteName","Ojhdt's Blog");
//        map.put("siteLink","https://blog.ojhdt.com/");
//        map.put("icon","https://blog.ojhdt.com/icon.png");
//        map.put("description","隐约雷鸣，阴霾天空");
//        mailService.sendMail("752279593@qq.com","友链加入通知","apply",map);

//        Comment comment = commentDao.selectOne(new QueryWrapper<Comment>().eq("id", "nma6wuj4km0jg615"));
//
//        String newsBody = comment.getContent();
//
//        System.out.println(HtmlFilter.delHtmlTags(newsBody));
//        System.out.println(newsBody);

    }



}
