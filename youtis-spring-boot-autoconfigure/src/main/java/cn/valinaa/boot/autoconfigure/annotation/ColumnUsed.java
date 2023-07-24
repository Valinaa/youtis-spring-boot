package cn.valinaa.boot.autoconfigure.annotation;

import cn.valinaa.boot.autoconfigure.enums.ColumnTypeEnum;
import cn.valinaa.boot.autoconfigure.enums.SignTypeEnum;

import java.lang.annotation.*;

/**
 * Column properties.
 *
 * @author Valinaa
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface ColumnUsed {
    /**
     * The column name.
     */
    String value() default "";

    /**
     * The column type.
     */
    ColumnTypeEnum type() default ColumnTypeEnum.NONE;

    /**
     * The column length.
     */
    int length() default 0;
    
    SignTypeEnum signType() default SignTypeEnum.NONE;

    /**
     * The column comment.
     */
    String comment() default "";

    /**
     * The column default value.
     */
    String defaultValue() default "null";

    /**
     * The column is nullable.
     */
    boolean nullable() default true;
    
    boolean autoIncrement() default false;
}
