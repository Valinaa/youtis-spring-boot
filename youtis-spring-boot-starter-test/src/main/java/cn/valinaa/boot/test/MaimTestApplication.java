package cn.valinaa.boot.test;

import cn.valinaa.boot.autoconfigure.annotation.YoutisScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Valinaa
 */
@SpringBootApplication
@YoutisScan("cn.valinaa.boot.test.entity")
public class MaimTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(MaimTestApplication.class, args);
    }
}
