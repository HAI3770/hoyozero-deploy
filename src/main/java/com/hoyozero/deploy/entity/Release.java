package com.hoyozero.deploy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_release")
public class Release {
    @TableId(type = IdType.ASSIGN_ID) private Long id;
    private Long projectId;
    private Long buildId;
    private String environment;
    private String status;
    private String targetImage;
    private String triggerBy;
    private String failureReason;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createTime;
}
