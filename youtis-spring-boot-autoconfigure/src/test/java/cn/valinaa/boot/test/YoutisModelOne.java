package cn.valinaa.boot.test;

import cn.valinaa.boot.autoconfigure.annotation.YoutisTable;

import java.time.LocalDateTime;

/**
 * 测试类实体
 *
 * @author Valinaa
 */
@YoutisTable
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
