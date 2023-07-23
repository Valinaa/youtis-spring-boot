package cn.valinaa.boot.test.entity;

import cn.valinaa.boot.autoconfigure.annotation.ColumnUsed;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author Valinaa
 */

@Data
public class Model2 {
    
    @ColumnUsed
    private String m2Name;
    
    @ColumnUsed
    private Integer m2Id;
    
    private LocalDateTime m2Time;
    
    @ColumnUsed
    private Timestamp m2Other;
}
