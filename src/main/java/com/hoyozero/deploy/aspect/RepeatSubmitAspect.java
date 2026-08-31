package com.hoyozero.deploy.aspect;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoyozero.deploy.annotation.RepeatSubmit;
import com.hoyozero.deploy.common.Result;
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
import java.util.concurrent.ConcurrentHashMap;

/**
 * 防重复提交切面
 */
@Slf4j
@Aspect
@Component
public class RepeatSubmitAspect {
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * 本地缓存，存储请求key和过期时间
     */
    private static final ConcurrentHashMap<String, Long> CACHE = new ConcurrentHashMap<>();
    
    /**
     * 定义切点：所有带有 @RepeatSubmit 注解的方法
     */
    @Pointcut("@annotation(com.hoyozero.deploy.annotation.RepeatSubmit)")
    public void repeatSubmitPointcut() {
    }
    
    /**
     * 环绕通知：防重复提交检查
     */
    @Around("repeatSubmitPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RepeatSubmit repeatSubmit = method.getAnnotation(RepeatSubmit.class);
        
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        
        if (request == null) {
            return joinPoint.proceed();
        }
        
        // 生成缓存key
        String cacheKey = generateCacheKey(request, joinPoint);
        
        long currentTime = System.currentTimeMillis();
        long interval = repeatSubmit.interval();
        
        // 清理过期缓存
        cleanExpiredCache();
        
        // 检查是否重复提交
        Long expireTime = CACHE.get(cacheKey);
        if (expireTime != null && currentTime < expireTime) {
            log.warn("检测到重复提交，key: {}", cacheKey);
            return Result.error(repeatSubmit.message());
        }
        
        // 设置缓存
        CACHE.put(cacheKey, currentTime + interval);
        
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            // 如果执行异常，移除缓存，允许重试
            CACHE.remove(cacheKey);
            throw e;
        }
    }
    
    /**
     * 生成缓存key
     * 基于用户ID + 请求URI + 请求方法 + 请求参数摘要
     */
    private String generateCacheKey(HttpServletRequest request, ProceedingJoinPoint joinPoint) {
        StringBuilder keyBuilder = new StringBuilder();
        
        // 用户ID
        try {
            if (StpUtil.isLogin()) {
                keyBuilder.append(StpUtil.getLoginIdAsString()).append(":");
            }
        } catch (Exception e) {
            keyBuilder.append("anonymous:");
        }
        
        // 请求URI和方法
        keyBuilder.append(request.getRequestURI()).append(":");
        keyBuilder.append(request.getMethod()).append(":");
        
        // 请求参数摘要
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                StringBuilder params = new StringBuilder();
                for (Object arg : args) {
                    if (arg != null && 
                        !(arg instanceof HttpServletRequest) && 
                        !(arg instanceof jakarta.servlet.http.HttpServletResponse)) {
                        params.append(objectMapper.writeValueAsString(arg));
                    }
                }
                if (params.length() > 0) {
                    keyBuilder.append(DigestUtil.md5Hex(params.toString()));
                }
            }
        } catch (Exception e) {
            log.warn("生成请求参数摘要失败", e);
        }
        
        return keyBuilder.toString();
    }
    
    /**
     * 清理过期缓存（简单实现，每次请求时清理）
     */
    private void cleanExpiredCache() {
        long currentTime = System.currentTimeMillis();
        CACHE.entrySet().removeIf(entry -> entry.getValue() < currentTime);
    }
}
