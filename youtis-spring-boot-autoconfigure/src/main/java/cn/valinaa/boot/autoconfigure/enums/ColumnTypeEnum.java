package cn.valinaa.boot.autoconfigure.enums;

/**
 * Column type enum.
 *
 * @author Valinaa
 */

public enum ColumnTypeEnum {
    /**
     * Number type.
     */
    BIT("BIT"),
    TINYINT("TINYINT"),
    SMALLINT("SMALLINT"),
    MEDIUMINT("MEDIUMINT"),
    INT("INT"),
    INTEGER("INTEGER"),
    BIGINT("BIGINT"),
    DECIMAL("DECIMAL"),
    NUMERIC("NUMERIC"),
    FLOAT("FLOAT"),
    DOUBLE("DOUBLE"),
    BOOL("BOOL"),
    BOOLEAN("BOOLEAN"),
    /**
     * Date type.
     */
    DATE("DATE"),
    TIME("TIME"),
    DATETIME("DATETIME"),
    TIMESTAMP("TIMESTAMP"),
    YEAR("YEAR"),
    /**
     * String type.
     */
    VARCHAR("VARCHAR"),
    CHAR("CHAR"),
    /**
     * Text type.
     */
    TINYTEXT("TINYTEXT"),
    TEXT("TEXT"),
    MEDIUMTEXT("MEDIUMTEXT"),
    LONGTEXT("LONGTEXT"),
    /**
     * Blob type.
     */
    TINYBLOB("TINYBLOB"),
    BLOB("BLOB"),
    MEDIUMBLOB("MEDIUMBLOB"),
    LONGBLOB("LONGBLOB"),
    /**
     * Collection type.
     */
    SET("SET"),
    ENUM("ENUM");
    
    private final String type;
    public String getType(){
        return this.type;
    }
    private ColumnTypeEnum(String type) {
        this.type = type;
    }
}
