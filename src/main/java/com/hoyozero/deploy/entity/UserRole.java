package com.hoyozero.deploy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_user_role")
public class UserRole {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long userId;
    
    private Long roleId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
