package cn.valinaa.boot.test;

import cn.valinaa.boot.autoconfigure.annotation.ColumnUsed;
import cn.valinaa.boot.autoconfigure.annotation.TableClass;

import java.time.LocalDateTime;

/**
 * 测试类实体
 *
 * @author Valinaa
 */
@TableClass
public class YoutisModelOne {
    @ColumnUsed
    private Long id;
    @ColumnUsed
    private String username;
    @ColumnUsed
    private String password;
    @ColumnUsed
    private LocalDateTime createTime;
}
