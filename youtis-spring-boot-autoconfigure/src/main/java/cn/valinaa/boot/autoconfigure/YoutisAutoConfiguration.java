package cn.valinaa.boot.autoconfigure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author Valinaa
 */

@Configuration
@EnableConfigurationProperties({YoutisProperties.class, DataSourceProperties.class,
        OutputProperties.class})
public class YoutisAutoConfiguration {
    private final YoutisProperties youtisProperties;
    private final DataSourceProperties dataSourceProperties;
    YoutisAutoConfiguration(YoutisProperties youtisProperties, DataSourceProperties dataSourceProperties) {
        this.youtisProperties = youtisProperties;
        this.dataSourceProperties = dataSourceProperties;
    }
    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url(dataSourceProperties.getUrl())
                .username(dataSourceProperties.getUsername())
                .password(dataSourceProperties.getPassword())
                .driverClassName(dataSourceProperties.getDriverClassName())
                .build();
    }
    
    @Bean
    public Youtis youtis() {
        return new Youtis(youtisProperties.isEnabled()
                , youtisProperties.getOutputProperties().isEnabled()
                , youtisProperties.getOutputProperties().getPath());
    }
}
