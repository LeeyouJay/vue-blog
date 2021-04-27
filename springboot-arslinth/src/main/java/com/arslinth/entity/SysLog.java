package com.arslinth.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Arslinth
 * @ClassName SysLog
 * @Description TODO
 * @Date 2021/3/4
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysLog {

    private String id;

    private String username;

    private String ipAddress;

    private String ipSource;

    private String message;

    private String browserName;

    private String type;

    private boolean isSuccess;

    private String systemName;

    @TableField(fill = FieldFill.INSERT)
    private String createTime;
}
