package ru.timestop.video.generator.server.demo;

import ru.timestop.video.generator.server.template.entity.TemplateEntity;

import java.io.InputStream;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public interface DemoService {
    TemplateEntity createDemo(String filename, InputStream stream);

    TemplateEntity getDemo();
}
