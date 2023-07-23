package cn.valinaa.boot.test.entity;

import cn.valinaa.boot.autoconfigure.annotation.ColumnUsed;
import cn.valinaa.boot.autoconfigure.annotation.TableClass;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Description
 *
 * @author Valinaa
 */

@TableClass
@Data
public class Model3 {
    
    @ColumnUsed
    private String m3Name;
    
    @ColumnUsed
    private Integer m3Id;
    
    private LocalDateTime m3Time;
    
    @ColumnUsed
    private Timestamp m3Other;
}
