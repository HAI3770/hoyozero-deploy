package com.hoyozero.deploy.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoyozero.deploy.annotation.OperationLog;
import com.hoyozero.deploy.entity.User;
import com.hoyozero.deploy.service.OperationLogService;
import com.hoyozero.deploy.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 操作日志切面
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {
    
    @Autowired
    private OperationLogService operationLogService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * 定义切点：所有带有 @OperationLog 注解的方法
     */
    @Pointcut("@annotation(com.hoyozero.deploy.annotation.OperationLog)")
    public void operationLogPointcut() {
    }
    
    /**
     * 环绕通知：记录操作日志
     */
    @Around("operationLogPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog operationLog = method.getAnnotation(OperationLog.class);
        
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        
        // 创建操作日志对象
        com.hoyozero.deploy.entity.OperationLog logEntity = new com.hoyozero.deploy.entity.OperationLog();
        
        // 获取用户信息
        try {
            if (StpUtil.isLogin()) {
                Long userId = StpUtil.getLoginIdAsLong();
                User user = userService.getById(userId);
                if (user != null) {
                    logEntity.setUserId(userId);
                    logEntity.setUsername(user.getUsername());
                }
            }
        } catch (Exception e) {
            log.warn("获取用户信息失败", e);
        }
        
        // 设置基本信息
        logEntity.setModule(operationLog.module());
        logEntity.setOperationType(operationLog.type());
        logEntity.setDescription(operationLog.description());
        
        // 设置请求信息
        if (request != null) {
            logEntity.setMethod(request.getMethod() + " " + request.getRequestURI());
            logEntity.setIpAddress(getIpAddress(request));
        }
        
        // 获取请求参数
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                // 过滤掉 HttpServletRequest、HttpServletResponse 等对象
                StringBuilder params = new StringBuilder();
                for (Object arg : args) {
                    if (arg != null && 
                        !(arg instanceof HttpServletRequest) && 
                        !(arg instanceof jakarta.servlet.http.HttpServletResponse)) {
                        params.append(objectMapper.writeValueAsString(arg)).append(" ");
                    }
                }
                String paramStr = params.toString().trim();
                if (paramStr.length() > 2000) {
                    paramStr = paramStr.substring(0, 2000) + "...";
                }
                logEntity.setParams(paramStr);
            }
        } catch (Exception e) {
            log.warn("获取请求参数失败", e);
            logEntity.setParams("参数解析失败");
        }
        
        Object result = null;
        try {
            // 执行目标方法
            result = joinPoint.proceed();
            
            // 记录返回结果
            logEntity.setStatus(1);
            try {
                String resultStr = objectMapper.writeValueAsString(result);
                if (resultStr.length() > 2000) {
                    resultStr = resultStr.substring(0, 2000) + "...";
                }
                logEntity.setResult(resultStr);
            } catch (Exception e) {
                logEntity.setResult("结果序列化失败");
            }
            
        } catch (Exception e) {
            // 记录异常信息
            logEntity.setStatus(0);
            logEntity.setErrorMsg(e.getMessage());
            if (logEntity.getErrorMsg() != null && logEntity.getErrorMsg().length() > 500) {
                logEntity.setErrorMsg(logEntity.getErrorMsg().substring(0, 500) + "...");
            }
            throw e;
        } finally {
            // 异步保存日志
            operationLogService.recordOperationLog(logEntity);
            
            // 打印执行时间
            long executeTime = System.currentTimeMillis() - startTime;
            log.info("操作日志 - 模块:{}, 类型:{}, 描述:{}, 耗时:{}ms", 
                    operationLog.module(), operationLog.type(), operationLog.description(), executeTime);
        }
        
        return result;
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
