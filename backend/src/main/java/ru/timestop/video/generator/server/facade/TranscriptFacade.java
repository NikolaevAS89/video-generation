package ru.timestop.video.generator.server.facade;

import ru.timestop.video.generator.server.transcript.model.AudioTemplate;
import ru.timestop.video.generator.server.transcript.model.WordMetadata;

import java.util.List;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public interface TranscriptFacade {
    UUID createAndSave(UUID task_id, List<WordMetadata> transcript);

    List<WordMetadata> getTranscript(UUID template_uuid);

    void setChosen(UUID template_uuid, AudioTemplate audioTemplate);

    AudioTemplate getChosen(UUID template_uuid);
}
