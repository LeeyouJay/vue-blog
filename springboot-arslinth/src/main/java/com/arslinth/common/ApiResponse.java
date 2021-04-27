package com.arslinth.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Arslinth
 * @ClassName ApiResponse
 * @Description 返回体
 * @Date 2021/2/22
 */
@Data
public class ApiResponse {

    @ApiModelProperty(value = "返回码")
    private Integer code;

    @ApiModelProperty(value = "返回消息")
    private String message;

    @ApiModelProperty(value = "返回数据")
    private Map<String,Object> data = new HashMap<String,Object>();

    private ApiResponse(){}


    public ApiResponse message(String message){
        this.setMessage(message);
        return this;
    }

    public static ApiResponse code(Integer code){
        ApiResponse result = new ApiResponse();
        result.setCode(code);
        return result;
    }

    public ApiResponse data(String key, Object value){
        this.data.put(key, value);
        return this;
    }

    public ApiResponse data(Map<String,Object> map){
        this.setData(map);
        return this;
    }
}
