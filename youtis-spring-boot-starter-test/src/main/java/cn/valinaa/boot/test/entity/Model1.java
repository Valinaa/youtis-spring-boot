package cn.valinaa.boot.test.entity;

import cn.valinaa.boot.autoconfigure.annotation.ColumnUsed;
import cn.valinaa.boot.autoconfigure.annotation.TableClass;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Valinaa
 */
@TableClass
@Data
public class Model1 {
    
    @ColumnUsed
    private String name;
    
    @ColumnUsed
    private Long id;
    
    @ColumnUsed
    private LocalDateTime time;
    
    private String other;
}
