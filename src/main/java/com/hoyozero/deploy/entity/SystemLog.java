package com.hoyozero.deploy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_system_log")
public class SystemLog {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long userId;
    
    private String username;
    
    private String operation;
    
    private String method;
    
    private String params;
    
    private String ip;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
