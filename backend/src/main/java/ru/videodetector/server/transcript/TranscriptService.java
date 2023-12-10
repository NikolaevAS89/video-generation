package ru.videodetector.server.transcript;

import ru.videodetector.server.template.entity.TemplateEntity;
import ru.videodetector.server.transcript.entity.TranscriptEntity;
import ru.videodetector.server.transcript.model.WordMetadata;

import java.util.List;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public interface TranscriptService {
    TranscriptEntity createAndSave(TemplateEntity template, List<WordMetadata> transcript);

    List<WordMetadata> getTranscript(TemplateEntity template);

    void setChosen(TemplateEntity template, List<Integer> chosen);

    List<Integer> getChosen(TemplateEntity template);

    void deleteByTemplate(TemplateEntity template);
}
