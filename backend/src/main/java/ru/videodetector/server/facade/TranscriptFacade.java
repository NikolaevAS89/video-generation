package ru.videodetector.server.facade;

import ru.videodetector.server.transcript.model.WordMetadata;

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
