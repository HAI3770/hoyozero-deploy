package com.hoyozero.deploy.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        registry.addViewController("/{spring:[a-zA-Z0-9-_]+}")
                .setViewName("forward:/index.html");

        registry.addViewController("/**/{spring:[a-zA-Z0-9-_]+}")
                .setViewName("forward:/index.html");
    }

    /**
     * 配置 Jackson 消息转换器，将 Long 类型序列化为 String
     * 避免雪花算法生成的 ID 在前端 JavaScript 中精度丢失
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 注册 Java 8 时间模块
        objectMapper.registerModule(new JavaTimeModule());
        
        // 将 Long 类型序列化为 String，解决前端精度丢失问题
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);
        
        converter.setObjectMapper(objectMapper);
        converters.add(0, converter);
    }
}