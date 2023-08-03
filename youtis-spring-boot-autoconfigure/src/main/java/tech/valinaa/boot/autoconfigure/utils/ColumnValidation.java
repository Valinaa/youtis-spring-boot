package tech.valinaa.boot.autoconfigure.utils;

import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.valinaa.boot.autoconfigure.annotation.YoutisColumn;
import tech.valinaa.boot.autoconfigure.annotation.YoutisPrimary;
import tech.valinaa.boot.autoconfigure.enums.ColumnTypeEnum;
import tech.valinaa.boot.autoconfigure.enums.SignTypeEnum;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Valinaa
 */

public final class ColumnValidation {
    
    private static final Logger logger = LoggerFactory.getLogger(
            ColumnValidation.class);
    
    private ColumnValidation() {
    }
    
    public static Map<String, List<String>> validate(Field field, int count, boolean optional) {
        var result = new HashMap<String, List<String>>(8);
        var lengthList = new ArrayList<String>();
        var typeList = new ArrayList<String>();
        var primaryList = new ArrayList<String>();
        if (field.isAnnotationPresent(YoutisPrimary.class)) {
            if (!field.isAnnotationPresent(YoutisColumn.class) && optional) {
                logger.warn("Found @YoutisPrimary but no @YoutisColumn on the field `{}`. Ignored.",
                        field.getName());
                return null;
            }
        }
        var youtisColumn = field.getAnnotation(YoutisColumn.class);
        var columnName = youtisColumn.value();
        var type = youtisColumn.type();
        var length = youtisColumn.length();
        
        
        columnName = StrUtil.toUnderlineCase(columnName.isBlank() ?
                field.getName() : columnName);
        
        if (field.isAnnotationPresent(YoutisPrimary.class)) {
            primaryList.add(columnName);
        }
        
        var lengthRequired = true;
        if (length == 0) {
            lengthList.add(field.getName());
        }
        Class<?> javaType = field.getType();
        var nowType = new ArrayList<ColumnTypeEnum>();
        switch (javaType.getSimpleName()) {
            // TODO support BINARY using Byte[]
            case "Boolean" -> {
                if (length == 0) {
                    length = 1;
                }
                nowType.add(ColumnTypeEnum.TINYINT);
                nowType.add(ColumnTypeEnum.BOOL);
                nowType.add(ColumnTypeEnum.BOOLEAN);
                // TODO If BIT length>1, get support for Byte[]
                nowType.add(ColumnTypeEnum.BIT);
            }
            case "boolean" -> {
                if (length == 0) {
                    length = 1;
                }
                nowType.add(ColumnTypeEnum.TINYINT);
                nowType.add(ColumnTypeEnum.BOOL);
                nowType.add(ColumnTypeEnum.BOOLEAN);
            }
            case "Byte", "byte" -> nowType.add(ColumnTypeEnum.TINYINT);
            case "Short", "short" -> nowType.add(ColumnTypeEnum.SMALLINT);
            case "Integer", "int", "Long", "long" -> {
                nowType.add(ColumnTypeEnum.INT);
                nowType.add(ColumnTypeEnum.INTEGER);
                nowType.add(ColumnTypeEnum.MEDIUMINT);
            }
            case "BigInteger" -> nowType.add(ColumnTypeEnum.BIGINT);
            // TODO float,double,decimal,support length and decimals
            case "Float", "float" -> nowType.add(ColumnTypeEnum.FLOAT);
            case "Double", "double" -> nowType.add(ColumnTypeEnum.DOUBLE);
            case "BigDecimal" -> nowType.add(ColumnTypeEnum.DECIMAL);
            case "String" -> {
                if (length == 0) {
                    length = 255;
                }
                nowType.add(ColumnTypeEnum.VARCHAR);
                nowType.add(ColumnTypeEnum.CHAR);
                nowType.add(ColumnTypeEnum.TINYTEXT);
                nowType.add(ColumnTypeEnum.MEDIUMTEXT);
                nowType.add(ColumnTypeEnum.TEXT);
                nowType.add(ColumnTypeEnum.LONGTEXT);
            }
            case "Blob" -> {
                nowType.add(ColumnTypeEnum.BLOB);
                nowType.add(ColumnTypeEnum.TINYBLOB);
                nowType.add(ColumnTypeEnum.MEDIUMBLOB);
                nowType.add(ColumnTypeEnum.LONGBLOB);
            }
            case "Year" -> nowType.add(ColumnTypeEnum.YEAR);
            case "Date", "LocalDate" -> nowType.add(ColumnTypeEnum.DATE);
            case "Time", "LocalTime" -> nowType.add(ColumnTypeEnum.TIME);
            case "Timestamp" -> nowType.add(ColumnTypeEnum.TIMESTAMP);
            case "LocalDateTime" -> nowType.add(ColumnTypeEnum.DATETIME);
            default -> {
                logger.warn("It seems that an unexpected type has been encountered, please check it.");
                nowType.add(ColumnTypeEnum.VARCHAR);
            }
        }
        // Validate data type
        if (!nowType.contains(type) && type != ColumnTypeEnum.NONE) {
            typeList.add(field.getName());
        }
        type = type == ColumnTypeEnum.NONE ? nowType.get(0) : type;
        
        var signType = youtisColumn.signType();
        // Whether to use length
        if (isIntegerType(type)) {
            if (signType != SignTypeEnum.UNSIGNED_ZEROFILL
                    || (type == ColumnTypeEnum.TINYINT && length != 0)) {
                lengthRequired = false;
            }
        }
        if (isTextType(type) || isDateType(type)) {
            lengthRequired = false;
        }
        
        var defaultValue = youtisColumn.defaultValue();
        var nullable = youtisColumn.nullable();
        // Check default value
        if (StrUtil.equalsAnyIgnoreCase(defaultValue.trim(), "null")) {
            defaultValue = "NULL";
        } else {
            nullable = false;
            // TODO support more type for default value
            if (!isIntegerType(type)) {
                defaultValue = "'" + defaultValue + "'";
            }
        }
        // Validate nullable
        if (!nullable) {
            if (defaultValue.equals("NULL")) {
                logger.warn("Column `{}`: default value is NULL, but nullable is False, it will be set to True."
                        , columnName);
                nullable = true;
            }
        }
        
        var autoIncrement = youtisColumn.autoIncrement();
        // Validate autoIncrement
        if (autoIncrement) {
            if (!isIntegerType(type)) {
                logger.warn("Column `" + field.getName() + "`: Auto_Increment only support INTEGER type, it will be set to false.");
                autoIncrement = false;
            }
            if (nullable) {
                logger.warn("Column `" + field.getName() + "`: Auto_Increment only support NOT NULL, it will be set to false.");
                autoIncrement = false;
            }
            if (count > 0) {
                logger.warn("Column `" + field.getName() + "`: Auto_Increment only support one column, it will be set to false.");
                autoIncrement = false;
            }
        }
        
        var comment = youtisColumn.comment();
        // Generate DDL
        String tableDDL = StrUtil.format("`{}` {}{}{} {}{}{}{}",
                columnName,
                type.getType(),
                lengthRequired ? StrUtil.format("({}) ", length) : "",
                signType.getType(),
                nullable ? "NULL " : "NOT NULL ",
                defaultValue.isBlank() ? "" : StrUtil.format("DEFAULT {} ", defaultValue),
                autoIncrement ? "AUTO_INCREMENT " : "",
                comment.isBlank() ? "" : StrUtil.format("COMMENT '{}'", comment));
        result.put("result", new ArrayList<>() {{
            add(tableDDL);
        }});
        result.put("lengthInfo", lengthList);
        result.put("typeWarning", typeList);
        result.put("primary", primaryList);
        // No duplicate AUTO_INCREMENT
        result.put("autoIncrement", new ArrayList<>() {{
            add("autoIncremented");
        }});
        return result;
    }
    
    public static boolean isIntegerType(ColumnTypeEnum typeEnum) {
        return !(typeEnum != ColumnTypeEnum.TINYINT &&
                typeEnum != ColumnTypeEnum.SMALLINT &&
                typeEnum != ColumnTypeEnum.INT &&
                typeEnum != ColumnTypeEnum.INTEGER &&
                typeEnum != ColumnTypeEnum.MEDIUMINT &&
                typeEnum != ColumnTypeEnum.BIGINT);
    }
    
    public static boolean isTextType(ColumnTypeEnum typeEnum) {
        return !(typeEnum != ColumnTypeEnum.BLOB &&
                typeEnum != ColumnTypeEnum.TINYBLOB &&
                typeEnum != ColumnTypeEnum.MEDIUMBLOB &&
                typeEnum != ColumnTypeEnum.LONGBLOB &&
                typeEnum != ColumnTypeEnum.TINYTEXT &&
                typeEnum != ColumnTypeEnum.MEDIUMTEXT &&
                typeEnum != ColumnTypeEnum.TEXT &&
                typeEnum != ColumnTypeEnum.LONGTEXT);
    }
    
    public static boolean isDateType(ColumnTypeEnum typeEnum) {
        return !(typeEnum != ColumnTypeEnum.DATE &&
                typeEnum != ColumnTypeEnum.TIME &&
                typeEnum != ColumnTypeEnum.YEAR &&
                typeEnum != ColumnTypeEnum.TIMESTAMP &&
                typeEnum != ColumnTypeEnum.DATETIME);
    }
}
