package com.hoyozero.deploy.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 分页插件配置
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 添加分页插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);

        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        paginationInnerInterceptor.setMaxLimit(500L);

        // 溢出总页数后是否进行处理（默认不处理）
        paginationInnerInterceptor.setOverflow(false);

        interceptor.addInnerInterceptor(paginationInnerInterceptor);

        return interceptor;
    }

    @Bean
    public Jdbc3KeyGenerator keyGenerator() {
        return new Jdbc3KeyGenerator();
    }

}
