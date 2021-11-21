package com.arslinth;

import cn.hutool.core.util.RandomUtil;
import com.arslinth.common.ApiResponse;
import com.arslinth.common.ResponseCode;
import com.arslinth.config.redis.RedisTool;
import com.arslinth.dao.*;
import com.arslinth.entity.Article;
import com.arslinth.entity.Comment;
import com.arslinth.entity.Motto;
import com.arslinth.entity.SysUser;
import com.arslinth.entity.VO.ArticleQuery;
import com.arslinth.entity.VO.CommentVO;
import com.arslinth.service.ArticleService;
import com.arslinth.service.MailService;
import com.arslinth.utils.HtmlFilter;
import com.arslinth.utils.OAuthUtil;
import com.arslinth.utils.PageUtil;
import com.arslinth.utils.ThumbnailsUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    private final MottoDao mottoDao;




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
        HashMap<String, String> map = new HashMap<>();
        map.put("code", 10000 + "");
        mailService.sendMail("752279593@qq.com", "验证码", "mailCode", map);
    }

    @Test
    void daoTest(){
        Map<String,String> author = commentDao.findAuthor("about");

        author = Optional.ofNullable(author).orElseGet(()->{
            Map<String, String> finalAuthor = new HashMap<>();
            finalAuthor.put("nick_name", "Arslinth");
            finalAuthor.put("title","留言");
            finalAuthor.put("email","752279593@qq.com");
            return finalAuthor;
        });
        System.out.println(author);
    }
    @Test
    void TimeTest(){
        Motto mottos = mottoDao.selectOne(new QueryWrapper<Motto>().eq("id",9));

        //String format = mottos.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        System.out.println(mottos.getTime());

    }

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
    }


}
