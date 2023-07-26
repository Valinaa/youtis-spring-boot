package tech.valinaa.boot.autoconfigure.annotation;

import org.springframework.context.annotation.Import;
import tech.valinaa.boot.autoconfigure.BeanManagement;
import tech.valinaa.boot.autoconfigure.YoutisAutoConfiguration;

import java.lang.annotation.*;

/**
 * Scan the package which contains the annotation {@code @YoutisTable}.
 *
 * @author Valinaa
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({YoutisAutoConfiguration.YoutisScanProcessor.class, YoutisAutoConfiguration.class, BeanManagement.class})
public @interface YoutisScan {
    String[] value() default "";
}
