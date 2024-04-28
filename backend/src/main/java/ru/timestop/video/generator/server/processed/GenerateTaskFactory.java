package ru.timestop.video.generator.server.processed;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.timestop.video.generator.server.processed.entity.ProcessedEntity;
import ru.timestop.video.generator.server.transcript.entity.TranscriptEntity;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public interface GenerateTaskFactory {
    void createAudioGenerateTask(ProcessedEntity processedEntity, TranscriptEntity transcriptEntity) throws JsonProcessingException;
    void createVideoGenerateTask(ProcessedEntity processedEntity, TranscriptEntity transcriptEntity) throws JsonProcessingException;
}
