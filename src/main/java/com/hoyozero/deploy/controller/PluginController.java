package com.hoyozero.deploy.controller;

import com.hoyozero.deploy.annotation.OperationLog;
import com.hoyozero.deploy.annotation.RepeatSubmit;
import com.hoyozero.deploy.common.Result;
import com.hoyozero.deploy.entity.Plugin;
import com.hoyozero.deploy.entity.PluginInstall;
import com.hoyozero.deploy.service.PluginInstallService;
import com.hoyozero.deploy.service.PluginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/plugin")
public class PluginController {
    
    @Autowired
    private PluginService pluginService;
    
    @Autowired
    private PluginInstallService pluginInstallService;
    
    /**
     * 获取所有可用插件(按分类分组)
     */
    @GetMapping("/list")
    public Result<Map<String, List<Plugin>>> list() {
        Map<String, List<Plugin>> result = new HashMap<>();
        result.put("BASE_ENV", pluginService.getByCategory("BASE_ENV"));
        result.put("MIDDLEWARE", pluginService.getByCategory("MIDDLEWARE"));
        return Result.success(result);
    }
    
    /**
     * 获取服务器已安装的插件列表
     */
    @GetMapping("/installed/{serverId}")
    public Result<List<PluginInstall>> installed(@PathVariable Long serverId) {
        return Result.success(pluginInstallService.getServerPlugins(serverId));
    }
    
    /**
     * 检查插件是否已安装
     */
    @GetMapping("/check/{serverId}/{pluginId}")
    public Result<PluginInstall> checkInstalled(
            @PathVariable Long serverId,
            @PathVariable Long pluginId) {
        PluginInstall install = pluginInstallService.getInstalled(serverId, pluginId);
        return Result.success(install);
    }
    
    /**
     * 安装插件
     */
    @RepeatSubmit
    @OperationLog(module = "插件管理", type = "安装", description = "安装插件")
    @PostMapping("/install")
    public Result<Long> install(@RequestBody Map<String, Object> params) {
        Long serverId = Long.valueOf(params.get("serverId").toString());
        Long pluginId = Long.valueOf(params.get("pluginId").toString());
        String version = params.get("version").toString();
        
        Long installId = pluginInstallService.install(serverId, pluginId, version);
        return Result.success(installId);
    }
    
    /**
     * 卸载插件
     */
    @OperationLog(module = "插件管理", type = "卸载", description = "卸载插件")
    @DeleteMapping("/uninstall/{installId}")
    public Result<String> uninstall(@PathVariable Long installId) {
        pluginInstallService.uninstall(installId);
        return Result.success("卸载任务已提交");
    }
    
    /**
     * 获取安装日志
     */
    @GetMapping("/install/log/{installId}")
    public Result<PluginInstall> getInstallLog(@PathVariable Long installId) {
        return Result.success(pluginInstallService.getById(installId));
    }
    
    /**
     * 管理员 - 插件管理
     */
    @GetMapping("/manage/list")
    public Result<List<Plugin>> manageList() {
        return Result.success(pluginService.list());
    }
    
    @RepeatSubmit
    @OperationLog(module = "插件管理", type = "新增", description = "添加插件")
    @PostMapping("/manage")
    public Result<String> savePlugin(@RequestBody Plugin plugin) {
        pluginService.saveOrUpdate(plugin);
        return Result.success("保存成功");
    }
    
    @OperationLog(module = "插件管理", type = "删除", description = "删除插件")
    @DeleteMapping("/manage/{id}")
    public Result<String> deletePlugin(@PathVariable Long id) {
        pluginService.removeById(id);
        return Result.success("删除成功");
    }
}
