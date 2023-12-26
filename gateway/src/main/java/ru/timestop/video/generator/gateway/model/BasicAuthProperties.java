package ru.timestop.video.generator.gateway.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Configuration
@ConfigurationProperties(prefix = "basic-auth")
public class BasicAuthProperties {
    private String name;
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}