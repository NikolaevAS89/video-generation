package ru.timestop.video.generator.server.processed;

import ru.timestop.video.generator.server.processed.entity.ProcessedEntity;
import ru.timestop.video.generator.server.audiotemplate.entity.AudioTemplateEntity;

import java.util.*;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public interface ProcessedService {
    String STATUS_CREATED = "Created request";
    String STATUS_AUDIO_GEN_QUEUED = "Was added to audio generation queue";
    String STATUS_VIDEO_GEN_QUEUED = "Was added to video generation queue";
    String STATUS_COMPLETE = "Successes";

    ProcessedEntity createNew(AudioTemplateEntity audioTemplateEntity, Map<String, String> words);

    ProcessedEntity updateStatus(UUID processedId, String newStatus);

    ProcessedEntity updateStatus(ProcessedEntity processedEntity, String newStatus);

    void deleteAllByAudioTemplate(AudioTemplateEntity templateEntity);

    Optional<ProcessedEntity> get(UUID processedId);

    List<ProcessedEntity> getAll();

    List<ProcessedEntity> getByIds(Set<UUID> processedIds);

    void delete(AudioTemplateEntity templateEntity);
}
