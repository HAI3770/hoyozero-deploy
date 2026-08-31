package com.hoyozero.deploy.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.hoyozero.deploy.common.Result;
import com.hoyozero.deploy.entity.LoginLog;
import com.hoyozero.deploy.entity.User;
import com.hoyozero.deploy.service.LoginLogService;
import com.hoyozero.deploy.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private LoginLogService loginLogService;
    
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User loginUser, HttpServletRequest request) {
        String username = loginUser.getUsername();
        User user = userService.login(username, loginUser.getPassword());
        
        // 记录登录日志
        LoginLog loginLog = new LoginLog();
        loginLog.setUsername(username);
        loginLog.setIpAddress(getIpAddress(request));
        loginLog.setBrowser(getBrowser(request));
        loginLog.setOs(getOs(request));
        
        if (user == null) {
            loginLog.setStatus(0);
            loginLog.setMessage("用户名或密码错误");
            loginLogService.recordLoginLog(loginLog);
            return Result.error("用户名或密码错误");
        }
        
        // 登录
        StpUtil.login(user.getId());
        
        loginLog.setUserId(user.getId());
        loginLog.setStatus(1);
        loginLog.setMessage("登录成功");
        loginLog.setLoginTime(LocalDateTime.now());
        loginLogService.recordLoginLog(loginLog);
        
        Map<String, Object> data = new HashMap<>();
        data.put("token", StpUtil.getTokenValue());
        data.put("user", user);
        
        return Result.success(data);
    }
    
    @PostMapping("/logout")
    public Result<String> logout() {
        StpUtil.logout();
        return Result.success("退出成功");
    }
    
    @GetMapping("/info")
    public Result<User> info() {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userService.getById(userId);
        return Result.success(user);
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
    
    /**
     * 获取浏览器信息
     */
    private String getBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) return "Unknown";
        
        if (userAgent.contains("Edge")) return "Edge";
        if (userAgent.contains("Chrome")) return "Chrome";
        if (userAgent.contains("Firefox")) return "Firefox";
        if (userAgent.contains("Safari")) return "Safari";
        return "Other";
    }
    
    /**
     * 获取操作系统信息
     */
    private String getOs(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) return "Unknown";
        
        if (userAgent.contains("Windows")) return "Windows";
        if (userAgent.contains("Mac")) return "macOS";
        if (userAgent.contains("Linux")) return "Linux";
        if (userAgent.contains("Android")) return "Android";
        if (userAgent.contains("iPhone") || userAgent.contains("iPad")) return "iOS";
        return "Other";
    }
}
