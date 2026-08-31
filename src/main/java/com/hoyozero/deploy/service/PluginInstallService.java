package com.hoyozero.deploy.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hoyozero.deploy.entity.Plugin;
import com.hoyozero.deploy.entity.PluginInstall;
import com.hoyozero.deploy.mapper.PluginInstallMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PluginInstallService extends ServiceImpl<PluginInstallMapper, PluginInstall> {
    
    @Autowired
    private PluginService pluginService;
    
    @Autowired
    @Lazy
    private PluginExecutorService pluginExecutorService;
    
    /**
     * 获取服务器上已安装的插件列表
     */
    public List<PluginInstall> getServerPlugins(Long serverId) {
        LambdaQueryWrapper<PluginInstall> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PluginInstall::getServerId, serverId)
               .orderByDesc(PluginInstall::getCreateTime);
        return this.list(wrapper);
    }
    
    /**
     * 检查插件是否已安装
     */
    public PluginInstall getInstalled(Long serverId, Long pluginId) {
        LambdaQueryWrapper<PluginInstall> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PluginInstall::getServerId, serverId)
               .eq(PluginInstall::getPluginId, pluginId)
               .eq(PluginInstall::getStatus, "SUCCESS");
        return this.getOne(wrapper);
    }
    
    /**
     * 触发插件安装
     */
    public Long install(Long serverId, Long pluginId, String version) {
        Plugin plugin = pluginService.getById(pluginId);
        if (plugin == null) {
            throw new RuntimeException("插件不存在");
        }
        
        // 创建安装记录
        PluginInstall install = new PluginInstall();
        install.setServerId(serverId);
        install.setPluginId(pluginId);
        install.setVersion(version);
        install.setStatus("PENDING");
        install.setOperateBy(StpUtil.getLoginIdAsString());
        this.save(install);
        
        // 异步执行安装（通过外部Service调用，确保异步生效）
        pluginExecutorService.executeInstall(install.getId());
        
        return install.getId();
    }
    
    /**
     * 触发插件卸载
     */
    public void uninstall(Long installId) {
        PluginInstall install = this.getById(installId);
        if (install == null) {
            throw new RuntimeException("安装记录不存在");
        }
        
        // 异步执行卸载（通过外部Service调用，确保异步生效）
        pluginExecutorService.executeUninstall(installId);
    }
}
