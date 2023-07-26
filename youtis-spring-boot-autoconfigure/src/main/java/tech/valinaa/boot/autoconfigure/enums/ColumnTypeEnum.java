package tech.valinaa.boot.autoconfigure.enums;

/**
 * Column type enum.
 *
 * @author Valinaa
 */

public enum ColumnTypeEnum {
    NONE(""),
    /**
     * Number type.
     */
    BIT("BIT"),
    BINARY("BINARY"),
    VARBINARY("VARBINARY"),
    TINYINT("TINYINT"),
    SMALLINT("SMALLINT"),
    MEDIUMINT("MEDIUMINT"),
    INT("INT"),
    INTEGER("INTEGER"),
    BIGINT("BIGINT"),
    DECIMAL("DECIMAL"),
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
    // TODO need support
    ENUM("ENUM");
    
    private final String type;
    
    ColumnTypeEnum(String type) {
        this.type = type;
    }
    
    public String getType() {
        return this.type;
    }
}
