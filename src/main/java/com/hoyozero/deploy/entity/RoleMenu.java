package com.hoyozero.deploy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_role_menu")
public class RoleMenu {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 角色ID
     */
    private Long roleId;
    
    /**
     * 菜单ID
     */
    private Long menuId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
