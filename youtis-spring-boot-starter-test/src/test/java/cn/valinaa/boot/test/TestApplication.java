package cn.valinaa.boot.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;

/**
 * @author Valinaa
 */
@SpringBootTest
public class TestApplication {
    @Test
    void test(){
        Class<?> aClass = cn.valinaa.boot.test.entity.Model3.class;
        Field[] fields = aClass.getDeclaredFields();
        for (Field field : fields) {
            System.out.println(field.getName());
            System.out.println(field.getType().getSimpleName());
        }
    }
}
