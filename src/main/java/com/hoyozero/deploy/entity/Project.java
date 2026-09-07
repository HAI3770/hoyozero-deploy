package com.hoyozero.deploy.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_project")
public class Project {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private String name;
    
    private String description;
    
    private String gitUrl;
    
    private String branch;
    
    private String gitUsername;
    
    private String gitPassword;
    
    private String projectType;
    
    private String buildCommand;
    
    private String buildDir;
    
    private String projectDir;
    
    private Long serverId;
    
    private Integer autoDeploy;
    
    private String deployScript;
    
    private String deployPath;
    
    private Integer appPort;

    private String env;

    private Long groupId;

    /** Docker/CI 配置 */
    private String dockerfilePath;
    private String dockerContext;
    private String registryUrl;
    private String registryUsername;
    private String registryToken;
    private String registryNamespace;
    private String imageName;
    private String imageTagRule;
    private String buildPlatform;
    private Integer autoPush;
    private String webhookToken;
    private Integer deployEnabled;
    private String composePath;
    private String composeService;
    private String healthCheckUrl;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    // 最近部署时间（非数据库字段）
    @TableField(exist = false)
    private LocalDateTime lastDeployTime;
}
