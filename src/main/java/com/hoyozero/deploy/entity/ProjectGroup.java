package com.hoyozero.deploy.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_project_group")
public class ProjectGroup {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private String name;
    
    private String description;
    
    private String owner;
    
    private String ownerContact;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    // 项目数量（非数据库字段）
    @TableField(exist = false)
    private Integer projectCount;
}
