package com.hoyozero.deploy.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_build")
public class Build {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long projectId;

    private String status;

    private String log;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;

    private Long duration;

    private String triggerBy;

    private Integer buildNumber;
    private String gitUrl;
    private String gitBranch;
    private String gitCommit;
    private String image;
    private String imageTag;
    private String imageDigest;
    private String stage;
    private String runnerName;
    private String failureReason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;

    // 项目名称（非数据库字段）
    @TableField(exist = false)
    private String projectName;

    // 环境（非数据库字段，从项目中获取）
    @TableField(exist = false)
    private String env;

    // 触发人名称（非数据库字段）
    @TableField(exist = false)
    private String triggerByName;
}
