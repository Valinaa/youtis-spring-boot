package cn.valinaa.boot.autoconfigure.annotation;

import cn.valinaa.boot.autoconfigure.YoutisScanBeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Scan the package which contains the annotation {@code @TableClass}.
 *
 * @author Valinaa
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(YoutisScanBeanDefinitionRegistryPostProcessor.class)
public @interface YoutisScan {
    String[] value() default "";
}
