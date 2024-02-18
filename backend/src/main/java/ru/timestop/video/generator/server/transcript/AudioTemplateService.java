package ru.timestop.video.generator.server.transcript;

import ru.timestop.video.generator.server.template.entity.TemplateEntity;
import ru.timestop.video.generator.server.transcript.entity.AudioTemplateEntity;
import ru.timestop.video.generator.server.transcript.model.AudioTemplate;
import ru.timestop.video.generator.server.transcript.model.WordMetadata;

import java.util.List;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public interface AudioTemplateService {
    AudioTemplateEntity createAndSave(TemplateEntity template, AudioTemplate audioTemplate);

    AudioTemplate getAudioTemplate(TemplateEntity template);

    void deleteByTemplate(TemplateEntity template);
}
