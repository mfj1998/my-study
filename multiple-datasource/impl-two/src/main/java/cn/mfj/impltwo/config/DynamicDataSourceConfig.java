package cn.mfj.impltwo.config;

import cn.mfj.impltwo.common.DataSourceConstant;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态初始化数据源的配置类
 * <p>
 * 将数据源进行初始化，然后放入到Map容器中
 * 注意：当前这个类需要提前排除掉自动注入数据源的配置，因为使用的是spring jdbc包下的DynamicDataSource
 * </p>
 *
 * @author favian.meng on 2021-09-28
 */
@Configuration
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class DynamicDataSourceConfig {

    @Bean
    @Order(1)
    @ConfigurationProperties(prefix = "spring.datasource.master")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Order(2)
    @ConfigurationProperties(prefix = "spring.datasource.slave")
    public DataSource slaveDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Order(3)
    @Primary
    public DataSource dynamicDataSource() {
        Map<Object, Object> map = new HashMap<>();
        map.put(DataSourceConstant.MASTER_DATA_SOURCE, masterDataSource());
        map.put(DataSourceConstant.SLAVE_DATA_SOURCE, slaveDataSource());
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        // 多数据源map容器，key：一般为租户  value：数据源对象
        dynamicDataSource.setTargetDataSources(map);
        // 指定默认数据源
        dynamicDataSource.setDefaultTargetDataSource(masterDataSource());
        return dynamicDataSource;
    }
}
