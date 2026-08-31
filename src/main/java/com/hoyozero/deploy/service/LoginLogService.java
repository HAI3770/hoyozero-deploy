package com.hoyozero.deploy.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hoyozero.deploy.entity.LoginLog;
import com.hoyozero.deploy.mapper.LoginLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LoginLogService extends ServiceImpl<LoginLogMapper, LoginLog> {
    
    /**
     * 异步记录登录日志
     */
    @Async
    public void recordLoginLog(LoginLog loginLog) {
        try {
            this.save(loginLog);
        } catch (Exception e) {
            log.error("记录登录日志失败", e);
        }
    }
}
