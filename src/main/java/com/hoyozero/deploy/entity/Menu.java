package com.hoyozero.deploy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("t_menu")
public class Menu {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 父菜单ID，0表示顶级菜单
     */
    private Long parentId;
    
    /**
     * 菜单名称
     */
    private String name;
    
    /**
     * 菜单路径
     */
    private String path;
    
    /**
     * 菜单图标
     */
    private String icon;
    
    /**
     * 排序
     */
    private Integer sortOrder;
    
    /**
     * 菜单类型：MENU-菜单，BUTTON-按钮
     */
    private String menuType;
    
    /**
     * 权限标识
     */
    private String permission;
    
    /**
     * 是否可见：1-是，0-否
     */
    private Integer visible;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    private Integer deleted;
    
    /**
     * 子菜单列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<Menu> children;
}
