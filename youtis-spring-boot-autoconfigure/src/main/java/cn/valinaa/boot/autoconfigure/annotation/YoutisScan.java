package cn.valinaa.boot.autoconfigure.annotation;

import cn.valinaa.boot.autoconfigure.YoutisScanProcessor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Scan the package which contains the annotation {@code @YoutisTable}.
 *
 * @author Valinaa
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(YoutisScanProcessor.class)
public @interface YoutisScan {
    String[] value() default "";
}
