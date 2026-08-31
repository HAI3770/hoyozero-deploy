package com.hoyozero.deploy.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hoyozero.deploy.entity.Plugin;
import com.hoyozero.deploy.mapper.PluginMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PluginService extends ServiceImpl<PluginMapper, Plugin> {
    
    /**
     * 根据分类获取插件列表
     */
    public List<Plugin> getByCategory(String category) {
        LambdaQueryWrapper<Plugin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Plugin::getCategory, category)
               .eq(Plugin::getStatus, "ENABLED")
               .orderByAsc(Plugin::getSort);
        return this.list(wrapper);
    }
    
    /**
     * 获取所有可用插件
     */
    public List<Plugin> getAllEnabled() {
        LambdaQueryWrapper<Plugin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Plugin::getStatus, "ENABLED")
               .orderByAsc(Plugin::getCategory, Plugin::getSort);
        return this.list(wrapper);
    }
}
