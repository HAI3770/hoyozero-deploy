package com.hoyozero.deploy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_project_member")
public class ProjectMember {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long projectId;
    
    private Long userId;
    
    /**
     * 角色类型：OWNER-拥有者，DEVELOPER-开发者，MEMBER-成员
     */
    private String roleType;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 用户信息（非数据库字段）
     */
    @TableField(exist = false)
    private User user;
}
