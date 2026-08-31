package com.hoyozero.deploy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_server")
public class Server {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private String name;
    
    private String host;
    
    private Integer port;
    
    private String username;
    
    private String authType;
    
    private String password;
    
    private String privateKey;
    
    private String uploadPath;
    
    private String status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
