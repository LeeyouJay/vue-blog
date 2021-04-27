package com.arslinth.controller;


import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.arslinth.common.ApiResponse;
import com.arslinth.common.ResponseCode;
import com.arslinth.config.redis.RedisTool;
import com.arslinth.entity.ImageResult;
import com.arslinth.utils.SliderImgUtil;
import com.arslinth.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
@RestController
@RequestMapping("/slider")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SliderController {

    private  int xPosCache = 0;

    private final RedisTool redisTool;

    @Value("${file.uploadFolder}")
    private String basePath;

    @GetMapping("/image")
    public ApiResponse image(){
        log.info("/slider/image");
        ImageResult imageResult = null;
        try{
            TimeUtil.start(1);
            imageResult = new SliderImgUtil(basePath).imageResult();
            TimeUtil.end(1);
            xPosCache = imageResult.getXpos();
            String captchaUUid = IdUtil.simpleUUID();
            redisTool.set(captchaUUid,xPosCache,5L, TimeUnit.MINUTES);
            imageResult.setXpos(0);

            Map<String, Object> img = JSON.parseObject(JSON.toJSONString(imageResult));
            img.put("captchaUUid",captchaUUid);
            return ApiResponse.code(ResponseCode.SUCCESS).data(img).message("图片请求成功！");
        }
        catch(Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            return ApiResponse.code(ResponseCode.FAIL).message("图片请求失败！");
        }
    }


    @GetMapping("/verification")
    public ApiResponse verification(@RequestParam("moveX") int moveX){

        log.info("/slider/verification/{}",moveX);

        int MOVE_CHECK_ERROR = 2;
        if(( moveX < ( xPosCache + MOVE_CHECK_ERROR))
                && ( moveX >  (xPosCache - MOVE_CHECK_ERROR))){
            log.info("验证正确");
            return ApiResponse.code(ResponseCode.SUCCESS).data("verify",true);
        }
        return ApiResponse.code(ResponseCode.SUCCESS).data("verify",false);
    }




}
