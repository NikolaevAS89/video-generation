package ru.timestop.video.generator.gateway;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@SpringBootApplication
@EnableConfigurationProperties
public class Application {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class)
                .run(args);
    }
}

