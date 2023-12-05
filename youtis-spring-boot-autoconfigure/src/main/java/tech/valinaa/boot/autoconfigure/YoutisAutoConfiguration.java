package tech.valinaa.boot.autoconfigure;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.NonNull;
import tech.valinaa.boot.autoconfigure.annotation.YoutisScan;
import tech.valinaa.boot.autoconfigure.annotation.YoutisTable;
import tech.valinaa.boot.autoconfigure.property.OutputProperties;
import tech.valinaa.boot.autoconfigure.property.YoutisProperties;

import java.util.List;
import java.util.Optional;

/**
 * @author Valinaa
 */

@Configuration
@EnableConfigurationProperties({YoutisProperties.class, OutputProperties.class})
public class YoutisAutoConfiguration {
    
    private static final Logger log = LogManager.getLogger(
            YoutisAutoConfiguration.class);
    private final YoutisProperties youtisProperties;
    
    private final OutputProperties outputProperties;
    
    @Autowired
    YoutisAutoConfiguration(YoutisProperties youtisProperties, OutputProperties outputProperties) {
        this.youtisProperties = youtisProperties;
        this.outputProperties = outputProperties;
    }
    
    @Bean
    public Youtis youtis() {
        return new Youtis(youtisProperties.isEnabled()
                , youtisProperties.isExecute()
                , outputProperties.isEnabled()
                , outputProperties.getPath());
    }
    
    public static class YoutisScanProcessor
            implements ImportBeanDefinitionRegistrar, BeanFactoryAware {
        
        private BeanFactory beanFactory;
        
        @Override
        public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata,
                                            @NonNull BeanDefinitionRegistry registry) {
            
            if (log.isDebugEnabled()) {
                if (!AutoConfigurationPackages.has(this.beanFactory)) {
                    log.debug("Could not determine auto-configuration package, automatic mapper scanning disabled.");
                    return;
                }
                log.debug("Searching for classes annotated with @YoutisTable");
                List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
                packages.forEach(pkg -> log.debug("Using auto-configuration base package '{}'", pkg));
            }
            var annotations = importingClassMetadata.getAnnotations();
            if (annotations.isPresent(YoutisScan.class)) {
                var basePackages = annotations.get(YoutisScan.class)
                        .getStringArray("value");
                if (basePackages.length > 0) {
                    log.debug("Found @YoutisScan, will scan packages: {}",
                            () -> String.join(",", basePackages));
                    var scanner = new ClassPathBeanDefinitionScanner(registry, false);
                    var tableClassAnnotationFilter = new AnnotationTypeFilter(
                            YoutisTable.class, false, true);
                    scanner.addIncludeFilter(tableClassAnnotationFilter);
                    for (String basePackage : basePackages) {
                        var beanDefinitions = scanner.findCandidateComponents(basePackage);
                        for (BeanDefinition beanDefinition : beanDefinitions) {
                            Optional.ofNullable(beanDefinition.getBeanClassName())
                                    .ifPresent(className -> {
                                        registry.registerBeanDefinition(className, beanDefinition);
                                        log.debug("Found @YoutisTable class: {}", className);
                                    });
                        }
                    }
                } else {
                    log.warn("Not found any package to scan, have you set a package name in @YoutisScan ?");
                }
            } else {
                log.warn("Not found @YoutisScan, have you set it on SpringApplication ?");
            }
        }
        
        @Override
        public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
        }
    }
}
