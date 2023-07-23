package cn.valinaa.boot.autoconfigure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Valinaa
 */

@Configuration
@EnableConfigurationProperties(YoutisProperties.class)
public class YoutisAutoConfiguration {
    private final YoutisProperties youtisProperties;
    
    YoutisAutoConfiguration(YoutisProperties youtisProperties) {
        this.youtisProperties = youtisProperties;
    }
}
