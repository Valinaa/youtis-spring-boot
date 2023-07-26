package tech.valinaa.boot.test.entity;

import tech.valinaa.boot.autoconfigure.annotation.YoutisColumn;
import tech.valinaa.boot.autoconfigure.annotation.YoutisPrimary;
import tech.valinaa.boot.autoconfigure.annotation.YoutisTable;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Description
 *
 * @author Valinaa
 */

@YoutisTable
public class Model3 {
    
    @YoutisColumn(defaultValue = "")
    private String m3Name;
    
    @YoutisColumn(defaultValue = "1")
    private Integer m3Id;
    private int m3Age;
    @YoutisPrimary
    private LocalDateTime m3Time;
    @YoutisColumn
    private Timestamp m3Other;

    public int getM3Age() {
        return m3Age;
    }
    
    public void setM3Age(int m3Age) {
        this.m3Age = m3Age;
    }
    
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
