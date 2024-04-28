package ru.timestop.video.generator.server.facade;

import ru.timestop.video.generator.server.template.entity.TemplateEntity;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public interface TemplateFacade {
    TemplateEntity createTemplate(String filename, InputStream stream);

    TemplateEntity getTemplate(UUID uuid);

    List<TemplateEntity> getTemplates();

    void delete(UUID uuid);
}
