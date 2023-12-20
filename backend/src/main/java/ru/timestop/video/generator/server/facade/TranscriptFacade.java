package ru.timestop.video.generator.server.facade;

import ru.timestop.video.generator.server.transcript.model.WordMetadata;

import java.util.List;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public interface TranscriptFacade {
    UUID createAndSave(UUID task_id, List<WordMetadata> transcript);

    List<WordMetadata> getTranscript(UUID task_id);

    void setChosen(UUID task_id, List<Integer> chosen);

    List<Integer> getChosen(UUID task_id);
}
