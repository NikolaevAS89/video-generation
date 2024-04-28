package ru.timestop.video.generator.server.transcript;

import ru.timestop.video.generator.server.template.entity.TemplateEntity;
import ru.timestop.video.generator.server.transcript.entity.TranscriptEntity;
import ru.timestop.video.generator.server.transcript.model.WordMetadata;

import java.util.List;
import java.util.Optional;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public interface TranscriptService {
    TranscriptEntity createAndSave(TemplateEntity template, List<WordMetadata> transcript);

    Optional<TranscriptEntity> getTranscript(TemplateEntity template);

    void deleteByTemplate(TemplateEntity template);
}
