package tech.valinaa.boot.autoconfigure.utils;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.text.CharSequenceUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.valinaa.boot.autoconfigure.annotation.YoutisColumn;
import tech.valinaa.boot.autoconfigure.annotation.YoutisPrimary;
import tech.valinaa.boot.autoconfigure.enums.ColumnTypeEnum;
import tech.valinaa.boot.autoconfigure.enums.SignTypeEnum;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Valinaa
 */

public final class ColumnValidation {
    
    private static final Logger log = LogManager.getLogger(ColumnValidation.class);
    
    private ColumnValidation() {
    }
    
    public static Map<String, List<String>> validate(Field field, int count, boolean optional) {
        Map<String, List<String>> result = new HashMap<>(8);
        List<String> lengthList = new ArrayList<>();
        List<String> typeList = new ArrayList<>();
        List<String> primaryList = new ArrayList<>();
        if (field.isAnnotationPresent(YoutisPrimary.class) && (!field.isAnnotationPresent(YoutisColumn.class) && optional)) {
            log.warn("Found @YoutisPrimary but no @YoutisColumn on the field `{}`. Ignored.",
                    field.getName());
            return Collections.emptyMap();
        }
        var youtisColumn = field.getAnnotation(YoutisColumn.class);
        var columnName = youtisColumn.value();
        var length = youtisColumn.length();
        
        columnName = CharSequenceUtil.toUnderlineCase(columnName.isBlank()
                ? field.getName() : columnName);
        
        if (field.isAnnotationPresent(YoutisPrimary.class)) {
            primaryList.add(columnName);
        }
        
        if (length == 0) {
            lengthList.add(field.getName());
        }
        List<ColumnTypeEnum> nowType = new ArrayList<>();
        length = typeTransform(field, nowType, length);
        // Validate data type
        var type = youtisColumn.type();
        if (!nowType.contains(type) && type != ColumnTypeEnum.NONE) {
            typeList.add(field.getName());
        }
        type = type == ColumnTypeEnum.NONE ? nowType.get(0) : type;
        
        var signType = youtisColumn.signType();
        // Whether to use length
        var lengthRequired = isLengthRequired(type,signType,length);
        
        // Check default value and nullable
        var pair = checkDefaultValueAndNullable(
                youtisColumn.defaultValue(),
                youtisColumn.nullable(),
                type,
                columnName);
        var defaultValue = pair.getKey();
        var nullable = pair.getValue();
        
        // Validate autoIncrement
        var autoIncrement = isAutoIncrement(youtisColumn.autoIncrement(),type,nullable,count,field.getName());
        
        var comment = youtisColumn.comment();
        // Generate DDL
        var tableDDL = CharSequenceUtil.format("`{}` {}{}{} {}{}{}{}",
                columnName,
                type.getType(),
                lengthRequired ? CharSequenceUtil.format("({}) ", length) : "",
                signType.getType(),
                Boolean.TRUE.equals(nullable) ? "NULL " : "NOT NULL ",
                defaultValue.isBlank() ? "" : CharSequenceUtil.format("DEFAULT {} ", defaultValue),
                autoIncrement ? "AUTO_INCREMENT " : "",
                comment.isBlank() ? "" : CharSequenceUtil.format("COMMENT '{}'", comment));
        result.put("result", Collections.singletonList(tableDDL));
        result.put("lengthInfo", lengthList);
        result.put("typeWarning", typeList);
        result.put("primary", primaryList);
        // No duplicate AUTO_INCREMENT
        result.put("autoIncrement", Collections.singletonList("autoIncremented"));
        return result;
    }
    
    private static int typeTransform(Field field, List<ColumnTypeEnum> nowType, int length) {
        switch (field.getType().getSimpleName()) {
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
                log.warn("It seems that an unexpected type has been encountered, please check it.");
                nowType.add(ColumnTypeEnum.VARCHAR);
            }
        }
        return length;
    }
    
    private static Pair<String,Boolean> checkDefaultValueAndNullable(String defaultValue, boolean nullable, ColumnTypeEnum type
            , String columnName) {
        if (CharSequenceUtil.equalsAnyIgnoreCase(defaultValue.trim(), "null")) {
            defaultValue = "NULL";
        } else {
            nullable = false;
            // TODO support more type for default value
            if (!isIntegerType(type)) {
                defaultValue = "'" + defaultValue + "'";
            }
        }
        // Validate nullable
        if (!nullable && (defaultValue.equals("NULL"))) {
            log.warn("Column `{}`: default value is NULL, but nullable is False, it will be set to True."
                    , columnName);
            nullable = true;
        }
        return Pair.of(defaultValue,nullable);
    }
    private static boolean isAutoIncrement(boolean autoIncrement,ColumnTypeEnum type, boolean nullable, int count,String fieldName){
        if (autoIncrement) {
            if (!isIntegerType(type)) {
                log.warn("Column `{}`: Auto_Increment only support INTEGER type, it will be set to false.", fieldName);
                return false;
            }
            if (nullable) {
                log.warn("Column `{}`: Auto_Increment only support NOT NULL, it will be set to false.", fieldName);
                return false;
            }
            if (count > 0) {
                log.warn("Column `{}`: Auto_Increment only support one column, it will be set to false.", fieldName);
                return false;
            }
        }
        return autoIncrement;
    }
    
    private static boolean isLengthRequired(ColumnTypeEnum type,SignTypeEnum signType, int length) {
        if (isIntegerType(type) &&
                (signType != SignTypeEnum.UNSIGNED_ZEROFILL ||
                        (type == ColumnTypeEnum.TINYINT && length != 0))) {
                return false;
        }
        return !isTextType(type) && !isDateType(type);
    }
    
    public static boolean isIntegerType(ColumnTypeEnum type) {
        return !(type != ColumnTypeEnum.TINYINT &&
                type != ColumnTypeEnum.SMALLINT &&
                type != ColumnTypeEnum.INT &&
                type != ColumnTypeEnum.INTEGER &&
                type != ColumnTypeEnum.MEDIUMINT &&
                type != ColumnTypeEnum.BIGINT);
    }
    
    public static boolean isTextType(ColumnTypeEnum type) {
        return !(type != ColumnTypeEnum.BLOB &&
                type != ColumnTypeEnum.TINYBLOB &&
                type != ColumnTypeEnum.MEDIUMBLOB &&
                type != ColumnTypeEnum.LONGBLOB &&
                type != ColumnTypeEnum.TINYTEXT &&
                type != ColumnTypeEnum.MEDIUMTEXT &&
                type != ColumnTypeEnum.TEXT &&
                type != ColumnTypeEnum.LONGTEXT);
    }
    
    public static boolean isDateType(ColumnTypeEnum type) {
        return !(type != ColumnTypeEnum.DATE &&
                type != ColumnTypeEnum.TIME &&
                type != ColumnTypeEnum.YEAR &&
                type != ColumnTypeEnum.TIMESTAMP &&
                type != ColumnTypeEnum.DATETIME);
    }
}
