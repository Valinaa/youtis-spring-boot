package cn.valinaa.boot.test.entity;

import cn.valinaa.boot.autoconfigure.annotation.ColumnPrimary;
import cn.valinaa.boot.autoconfigure.annotation.ColumnUsed;
import cn.valinaa.boot.autoconfigure.annotation.TableClass;
import cn.valinaa.boot.autoconfigure.enums.ColumnTypeEnum;

import java.time.LocalDateTime;

/**
 * @author Valinaa
 */
@TableClass(value = "modelFirst",comment = "测试表")
public class Model1 {
    
    @ColumnUsed(value = "VIP",type = ColumnTypeEnum.TINYTEXT,comment = "姓名",
            defaultValue = "Valinaa",nullable = false, autoIncrement = true)
    @ColumnPrimary
    private String name;
    
    @ColumnUsed
    @ColumnPrimary
    private Long id;
    
    @ColumnUsed
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
