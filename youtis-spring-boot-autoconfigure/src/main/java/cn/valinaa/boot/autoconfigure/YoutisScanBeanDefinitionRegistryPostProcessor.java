package cn.valinaa.boot.autoconfigure;

import cn.hutool.core.util.StrUtil;
import cn.valinaa.boot.autoconfigure.annotation.ColumnUsed;
import cn.valinaa.boot.autoconfigure.annotation.TableClass;
import cn.valinaa.boot.autoconfigure.annotation.YoutisScan;
import cn.valinaa.boot.autoconfigure.enums.ColumnTypeEnum;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.NonNull;

import java.lang.reflect.Field;

/**
 * @author Valinaa
 */
@Configuration
public class YoutisScanBeanDefinitionRegistryPostProcessor
        implements ImportBeanDefinitionRegistrar {
    
    private static final Log logger = LogFactory.getLog(
            YoutisScanBeanDefinitionRegistryPostProcessor.class);
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        @NonNull BeanDefinitionRegistry registry) {
        
        var annotations=importingClassMetadata.getAnnotations();
        if(annotations.isPresent(YoutisScan.class)){
            var basePackages=annotations.get(YoutisScan.class)
                    .getStringArray("value");
            if (basePackages.length > 0) {
                    var scanner = new ClassPathBeanDefinitionScanner(registry, false);
                    var tableClassAnnotationFilter = new AnnotationTypeFilter(
                            TableClass.class,false,true);
                    scanner.addIncludeFilter(tableClassAnnotationFilter);
                    for (String basePackage : basePackages) {
                        var beanDefinitions = scanner.findCandidateComponents(basePackage);
                        for (BeanDefinition beanDefinition : beanDefinitions) {
                            var className = beanDefinition.getBeanClassName();
                            try {
                                getFieldsNeedGenerated(Class.forName(className));
                            } catch (ClassNotFoundException e) {
                                logger.error(e.getMessage(),e);
                            }
                        }
                    }
            }else{
                logger.warn("No package to scan, have you set package name in @YoutisScan ?");
            }
        }else{
            logger.warn("Not found @EntityScan, have you set it on Application ?");
        }
    }
    
    private void getFieldsNeedGenerated(Class<?> clazz){
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ColumnUsed.class)) {
                processColumnUsed(field);
            }
        }
    }
    
    private void processColumnUsed(Field field){
        ColumnUsed columnUsed = field.getAnnotation(ColumnUsed.class);
        String columnName = columnUsed.value();
        String comment = columnUsed.comment();
        String defaultValue = columnUsed.defaultValue();
        ColumnTypeEnum type = columnUsed.type();
        int length = columnUsed.length();
        boolean nullable = columnUsed.nullable();
        if(columnName.isEmpty()){
            columnName=StrUtil.toUnderlineCase(field.getName());
        }
        Class<?> javaType = field.getType();
        // do something
    }
}
