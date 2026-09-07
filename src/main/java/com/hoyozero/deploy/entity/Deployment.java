package com.hoyozero.deploy.entity;
import com.baomidou.mybatisplus.annotation.*; import lombok.Data; import java.time.LocalDateTime;
@Data @TableName("t_deployment") public class Deployment { @TableId(type=IdType.ASSIGN_ID) private Long id; private Long projectId; private String environment; private Long serverId; private String serviceName; private String oldImage; private String newImage; private String imageDigest; private String status; private String deploymentLog; private String operateBy; private LocalDateTime deploymentTime; }
