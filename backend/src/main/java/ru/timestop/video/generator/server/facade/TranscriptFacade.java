package ru.timestop.video.generator.server.facade;

import ru.timestop.video.generator.server.facade.model.AudioTemplate;
import ru.timestop.video.generator.server.transcript.model.WordMetadata;

import java.util.List;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public interface TranscriptFacade {
    UUID createAndSave(UUID taskId, List<WordMetadata> transcript);

    List<WordMetadata> getTranscript(UUID templateId);

    void setChosen(UUID templateId, AudioTemplate audioTemplate);

    AudioTemplate getChosen(UUID templateId);
}
