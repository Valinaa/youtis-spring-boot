package cn.valinaa.boot.autoconfigure.utils;

import cn.hutool.core.util.StrUtil;
import cn.valinaa.boot.autoconfigure.annotation.YoutisColumn;
import cn.valinaa.boot.autoconfigure.annotation.YoutisPrimary;
import cn.valinaa.boot.autoconfigure.enums.ColumnTypeEnum;
import cn.valinaa.boot.autoconfigure.enums.SignTypeEnum;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Valinaa
 */

public final class ColumnValidation {
    
    private static final Log logger = LogFactory.getLog(
            ColumnValidation.class);
    
    private ColumnValidation() {
    }
    
    public static Map<String, List<String>> validate(Field field,int count) {
        Map<String, List<String>> result = new HashMap<>(8);
        List<String> lengthList = new ArrayList<>();
        List<String> typeList = new ArrayList<>();
        List<String> primaryList=new ArrayList<>();
        if(field.isAnnotationPresent(YoutisPrimary.class)){
            primaryList.add(field.getName());
        }
        YoutisColumn youtisColumn = field.getAnnotation(YoutisColumn.class);
        String columnName = youtisColumn.value();
        ColumnTypeEnum type = youtisColumn.type();
        int length = youtisColumn.length();
        SignTypeEnum signType = youtisColumn.signType();
        String comment = youtisColumn.comment();
        String defaultValue = youtisColumn.defaultValue();
        boolean nullable = youtisColumn.nullable();
        boolean autoIncrement = youtisColumn.autoIncrement();
        
        columnName = StrUtil.toUnderlineCase(columnName.isBlank() ?
                field.getName() : columnName);
        boolean lengthRequired = true;
        if (length == 0) {
            lengthList.add(field.getName());
        }
        Class<?> javaType = field.getType();
        List<ColumnTypeEnum> nowType = new ArrayList<>();
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
            case "Float", "float" -> {
                nowType.add(ColumnTypeEnum.FLOAT);
            }
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
        };
        // Validate data type
        if (!nowType.contains(type) && type != ColumnTypeEnum.NONE) {
            typeList.add(field.getName());
        }
        type = type == ColumnTypeEnum.NONE ? nowType.get(0) : type;
        // Whether to use length
        if (isIntegerType(type)) {
            if (signType != SignTypeEnum.UNSIGNED_ZEROFILL
                    && (type == ColumnTypeEnum.TINYINT && length != 0)) {
                lengthRequired = false;
            }
        }
        if(isTextType(type)||isDateType(type)){
            lengthRequired=false;
        }
        // Check default value
        if(StrUtil.equalsAnyIgnoreCase(defaultValue.trim(), "null")) {
            defaultValue = "NULL";
        }else{
            nullable=false;
            // TODO support more type for default value
            if(isIntegerType(type)){
                defaultValue= "'"+defaultValue+"'";
            }
        }
        // Validate nullable
        if(!nullable){
            if(defaultValue.equals("NULL")){
                logger.warn("Column `"+field.getName()+"`: default value is NULL, " +
                        "but nullable is False, it will be set to True.");
                nullable=true;
            }
        }
        // Validate autoIncrement
        if(autoIncrement){
            if(isIntegerType(type)){
                logger.warn("Column `"+field.getName()+"`: Auto_Increment only support INTEGER type, it will be set to false.");
                autoIncrement=false;
            }
            if(nullable){
                logger.warn("Column `"+field.getName()+"`: Auto_Increment only support NOT NULL, it will be set to false.");
                autoIncrement=false;
            }
            if(count>0){
                logger.warn("Column `"+field.getName()+"`: Auto_Increment only support one column, it will be set to false.");
                autoIncrement=false;
            }
        }
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
    
    public static boolean isIntegerType(ColumnTypeEnum typeEnum){
        return typeEnum != ColumnTypeEnum.TINYINT &&
                typeEnum != ColumnTypeEnum.SMALLINT &&
                typeEnum != ColumnTypeEnum.INT &&
                typeEnum != ColumnTypeEnum.INTEGER &&
                typeEnum != ColumnTypeEnum.MEDIUMINT &&
                typeEnum != ColumnTypeEnum.BIGINT;
    }
    
    public static boolean isTextType(ColumnTypeEnum typeEnum){
        return typeEnum != ColumnTypeEnum.BLOB &&
                typeEnum != ColumnTypeEnum.TINYBLOB &&
                typeEnum != ColumnTypeEnum.MEDIUMBLOB &&
                typeEnum != ColumnTypeEnum.LONGBLOB &&
                typeEnum != ColumnTypeEnum.TINYTEXT &&
                typeEnum != ColumnTypeEnum.MEDIUMTEXT &&
                typeEnum != ColumnTypeEnum.TEXT &&
                typeEnum != ColumnTypeEnum.LONGTEXT;
    }
    
    public static boolean isDateType(ColumnTypeEnum typeEnum){
        return typeEnum != ColumnTypeEnum.DATE &&
                typeEnum != ColumnTypeEnum.TIME &&
                typeEnum != ColumnTypeEnum.YEAR &&
                typeEnum != ColumnTypeEnum.TIMESTAMP &&
                typeEnum != ColumnTypeEnum.DATETIME;
    }
}
