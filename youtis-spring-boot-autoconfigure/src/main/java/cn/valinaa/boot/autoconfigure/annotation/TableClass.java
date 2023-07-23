package cn.valinaa.boot.autoconfigure.annotation;

import java.lang.annotation.*;

/**
 * Mark the class as a table class.
 *
 * @author Valinaa
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface TableClass {
    /**
     * The table name.
     */
    String value() default "";
    
    /**
     * The table comment.
     */
    String comment() default "";
}
