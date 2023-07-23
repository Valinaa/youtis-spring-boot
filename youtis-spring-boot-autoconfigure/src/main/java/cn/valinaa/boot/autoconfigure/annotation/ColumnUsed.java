package cn.valinaa.boot.autoconfigure.annotation;

import cn.valinaa.boot.autoconfigure.enums.ColumnTypeEnum;

import java.lang.annotation.*;

/**
 * Description
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
    ColumnTypeEnum type() default ColumnTypeEnum.VARCHAR;

    /**
     * The column length.
     */
    int length() default 255;

    /**
     * The column comment.
     */
    String comment() default "";

    /**
     * The column default value.
     */
    String defaultValue() default "";

    /**
     * The column is nullable.
     */
    boolean nullable() default true;
}
