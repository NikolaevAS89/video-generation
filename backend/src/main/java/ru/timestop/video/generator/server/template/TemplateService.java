package ru.timestop.video.generator.server.template;

import ru.timestop.video.generator.server.template.entity.TemplateEntity;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public interface TemplateService {
    TemplateEntity createTemplate(String filename, InputStream stream);

    TemplateEntity update(TemplateEntity templateEntity);

    void delete(TemplateEntity templateEntity);

    Optional<TemplateEntity> getTemplate(UUID uuid);

    List<TemplateEntity> getTemplates();
}
