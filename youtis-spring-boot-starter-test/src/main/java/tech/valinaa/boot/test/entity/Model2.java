package tech.valinaa.boot.test.entity;

import tech.valinaa.boot.autoconfigure.annotation.YoutisColumn;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author Valinaa
 */

public class Model2 {
    
    @YoutisColumn
    private String m2Name;
    
    @YoutisColumn
    private Integer m2Id;
    
    private LocalDateTime m2Time;
    
    @YoutisColumn
    private Timestamp m2Other;
    
    public String getM2Name() {
        return m2Name;
    }
    
    public void setM2Name(String m2Name) {
        this.m2Name = m2Name;
    }
    
    public Integer getM2Id() {
        return m2Id;
    }
    
    public void setM2Id(Integer m2Id) {
        this.m2Id = m2Id;
    }
    
    public LocalDateTime getM2Time() {
        return m2Time;
    }
    
    public void setM2Time(LocalDateTime m2Time) {
        this.m2Time = m2Time;
    }
    
    public Timestamp getM2Other() {
        return m2Other;
    }
    
    public void setM2Other(Timestamp m2Other) {
        this.m2Other = m2Other;
    }
    
    @Override
    public String toString() {
        return "Model2{" +
                "m2Name='" + m2Name + '\'' +
                ", m2Id=" + m2Id +
                ", m2Time=" + m2Time +
                ", m2Other=" + m2Other +
                '}';
    }
}
