package tech.valinaa.boot.test.entity;

import tech.valinaa.boot.autoconfigure.annotation.YoutisColumn;
import tech.valinaa.boot.autoconfigure.annotation.YoutisPrimary;
import tech.valinaa.boot.autoconfigure.annotation.YoutisTable;
import tech.valinaa.boot.autoconfigure.enums.ColumnTypeEnum;

import java.time.LocalDateTime;

/**
 * @author Valinaa
 */
@YoutisTable(value = "modelFirst", comment = "测试表")
public class Model1 {
    
    @YoutisColumn(value = "VIP", type = ColumnTypeEnum.TINYTEXT, comment = "姓名",
            defaultValue = "Valinaa", nullable = false, autoIncrement = true)
    @YoutisPrimary
    private String name;
    
    @YoutisColumn
    @YoutisPrimary
    private Long id;
    
    @YoutisColumn
    private LocalDateTime time;
    
    private String other;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDateTime getTime() {
        return time;
    }
    
    public void setTime(LocalDateTime time) {
        this.time = time;
    }
    
    public String getOther() {
        return other;
    }
    
    public void setOther(String other) {
        this.other = other;
    }
    
    @Override
    public String toString() {
        return "Model1{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", time=" + time +
                ", other='" + other + '\'' +
                '}';
    }
}
