package com.longtao.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.google.common.collect.Maps;
import com.longtao.config.properties.DruidProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.Map;

/**
 * druid 配置多数据源
 *
 * @author devops
 */
@Configuration

public class DruidConfig {
    @Bean
    @ConfigurationProperties("spring.datasource.druid.sqlite")
    // @Primary
    public DataSource sqliteDataSource(DruidProperties druidProperties) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return druidProperties.dataSource(dataSource);
    }

    @Bean
    @ConfigurationProperties("spring.datasource.druid.postgres")
    public DataSource postgresDataSource(DruidProperties druidProperties) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return druidProperties.dataSource(dataSource);
    }

    @Bean(name = "dynamicDataSource")
    @Primary
    public DynamicDataSource dataSource(DataSource sqliteDataSource, DataSource postgresDataSource) {
        DynamicDataSource dataSource = new DynamicDataSource();
        Map<Object, Object> map = Maps.newConcurrentMap();
        map.put("sqliteDataSource", sqliteDataSource);
        map.put("postgresDataSource", postgresDataSource);
        dataSource.setTargetDataSources(map);
        return dataSource;
    }

}
