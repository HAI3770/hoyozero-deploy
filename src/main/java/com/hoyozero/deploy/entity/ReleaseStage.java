package com.hoyozero.deploy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_release_stage")
public class ReleaseStage {
    @TableId(type = IdType.ASSIGN_ID) private Long id;
    private Long releaseId;
    private String stageKey;
    private String stageName;
    private Integer stageOrder;
    private String status;
    private String failureReason;
    private String log;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
