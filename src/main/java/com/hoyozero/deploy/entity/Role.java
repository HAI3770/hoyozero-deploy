package com.hoyozero.deploy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_role")
public class Role {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private String name;
    
    private String code;
    
    private String description;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
