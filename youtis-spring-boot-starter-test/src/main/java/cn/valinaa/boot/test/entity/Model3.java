package cn.valinaa.boot.test.entity;

import cn.valinaa.boot.autoconfigure.annotation.ColumnPrimary;
import cn.valinaa.boot.autoconfigure.annotation.ColumnUsed;
import cn.valinaa.boot.autoconfigure.annotation.TableClass;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Description
 *
 * @author Valinaa
 */

@TableClass
public class Model3 {
    
    @ColumnUsed(defaultValue = "")
    private String m3Name;
    
    @ColumnUsed(defaultValue = "1")
    private Integer m3Id;
    
    public int getM3Age() {
        return m3Age;
    }
    
    public void setM3Age(int m3Age) {
        this.m3Age = m3Age;
    }
    
    private int m3Age;
    @ColumnPrimary
    private LocalDateTime m3Time;
    
    @ColumnUsed
    private Timestamp m3Other;
    
    public String getM3Name() {
        return m3Name;
    }
    
    public void setM3Name(String m3Name) {
        this.m3Name = m3Name;
    }
    
    public Integer getM3Id() {
        return m3Id;
    }
    
    public void setM3Id(Integer m3Id) {
        this.m3Id = m3Id;
    }
    
    public LocalDateTime getM3Time() {
        return m3Time;
    }
    
    public void setM3Time(LocalDateTime m3Time) {
        this.m3Time = m3Time;
    }
    
    public Timestamp getM3Other() {
        return m3Other;
    }
    
    public void setM3Other(Timestamp m3Other) {
        this.m3Other = m3Other;
    }
    
    @Override
    public String toString() {
        return "Model3{" +
                "m3Name='" + m3Name + '\'' +
                ", m3Id=" + m3Id +
                ", m3Age=" + m3Age +
                ", m3Time=" + m3Time +
                ", m3Other=" + m3Other +
                '}';
    }
}
