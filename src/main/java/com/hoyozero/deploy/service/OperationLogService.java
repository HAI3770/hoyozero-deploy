package com.hoyozero.deploy.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hoyozero.deploy.entity.OperationLog;
import com.hoyozero.deploy.mapper.OperationLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class OperationLogService extends ServiceImpl<OperationLogMapper, OperationLog> {
    
    /**
     * 异步记录操作日志
     */
    @Async
    public void recordOperationLog(OperationLog operationLog) {
        try {
            operationLog.setOperationTime(LocalDateTime.now());
            this.save(operationLog);
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        }
    }
}
