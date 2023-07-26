package tech.valinaa.boot.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tech.valinaa.boot.autoconfigure.annotation.YoutisScan;

/**
 * @author Valinaa
 */
@SpringBootApplication
@YoutisScan({"tech.valinaa.boot.test.entity", "tech.valinaa.boot.test.model"})
public class MaimTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(MaimTestApplication.class, args);
    }
}
