package tech.valinaa.boot.test.model;

import tech.valinaa.boot.autoconfigure.annotation.YoutisColumn;
import tech.valinaa.boot.autoconfigure.annotation.YoutisPrimary;
import tech.valinaa.boot.autoconfigure.annotation.YoutisTable;

@YoutisTable
public class Entity1 {
    @YoutisColumn
    private String name;
    
    @YoutisColumn
    @YoutisPrimary
    private Long id;
}
