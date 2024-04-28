package ru.timestop.video.generator.server.audiotemplate;

import ru.timestop.video.generator.server.audiotemplate.entity.AudioTemplateEntity;
import ru.timestop.video.generator.server.template.entity.TemplateEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public interface AudioTemplateService {

    AudioTemplateEntity createAndSave(TemplateEntity template,
                                      List<Integer> chosen,
                                      Map<String, Integer> mapping);

    Optional<AudioTemplateEntity> getAudioTemplate(TemplateEntity template);

    void deleteByTemplate(TemplateEntity template);
}
