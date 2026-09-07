package com.hoyozero.deploy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_release_task")
public class ReleaseTask {
    @TableId(type = IdType.ASSIGN_ID) private Long id;
    private Long releaseId;
    private Long stageId;
    private Long serverId;
    private Long rollbackBuildId;
    private String taskType;
    private String status;
    private Integer attempt;
    private String log;
    private String failureReason;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
