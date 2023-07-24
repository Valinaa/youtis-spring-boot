package cn.valinaa.boot.autoconfigure.enums;

public enum SignTypeEnum {
    
    NONE(""),
    UNSIGNED("UNSIGNED"),
    UNSIGNED_ZEROFILL("UNSIGNED ZEROFILL"),
    BINARY("BINARY");
    
    private final String type;
    
    public String getType() {
        return type;
    }
    
    SignTypeEnum(String type) {
        this.type = type;
    }
}
