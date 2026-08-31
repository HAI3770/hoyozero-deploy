package com.hoyozero.deploy.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 登录日志实体类
 */
@Data
@TableName("t_login_log")
public class LoginLog {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 登录IP
     */
    private String ipAddress;
    
    /**
     * 登录地点
     */
    private String location;
    
    /**
     * 浏览器
     */
    private String browser;
    
    /**
     * 操作系统
     */
    private String os;
    
    /**
     * 登录状态 0-失败 1-成功
     */
    private Integer status;
    
    /**
     * 提示消息
     */
    private String message;
    
    /**
     * 登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime loginTime;
}
