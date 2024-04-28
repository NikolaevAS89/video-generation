package ru.timestop.video.generator.server.facade.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.timestop.video.generator.server.audiotemplate.AudioTemplateService;
import ru.timestop.video.generator.server.audiotemplate.entity.AudioTemplateEntity;
import ru.timestop.video.generator.server.facade.VideoCompilerService;
import ru.timestop.video.generator.server.facade.model.request.RequestStatus;
import ru.timestop.video.generator.server.facade.model.request.RequestToGenerateVideo;
import ru.timestop.video.generator.server.facade.model.response.RequestsStatus;
import ru.timestop.video.generator.server.processed.GenerateTaskFactory;
import ru.timestop.video.generator.server.processed.ProcessedService;
import ru.timestop.video.generator.server.processed.entity.ProcessedEntity;
import ru.timestop.video.generator.server.rabbitmq.processed.audio.model.AudioGenerationStatus;
import ru.timestop.video.generator.server.rabbitmq.processed.video.model.VideoGenerationStatus;
import ru.timestop.video.generator.server.storage.SourceVideoStorageService;
import ru.timestop.video.generator.server.storage.model.FilesContent;
import ru.timestop.video.generator.server.template.TemplateService;
import ru.timestop.video.generator.server.template.entity.TemplateEntity;
import ru.timestop.video.generator.server.transcript.TranscriptService;
import ru.timestop.video.generator.server.transcript.entity.TranscriptEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Service
public class VideoCompilerServiceImpl implements VideoCompilerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoCompilerServiceImpl.class);
    private final SourceVideoStorageService sourceVideoStorageService;

    private final ProcessedService processedService;

    private final GenerateTaskFactory taskManager;

    private final TemplateService templateService;

    private final TranscriptService transcriptService;

    private final AudioTemplateService audioTemplateService;

    public VideoCompilerServiceImpl(@Autowired SourceVideoStorageService sourceVideoStorageService,
                                    @Autowired ProcessedService processedService,
                                    @Autowired TemplateService templateService,
                                    @Autowired GenerateTaskFactory taskManager,
                                    @Autowired AudioTemplateService audioTemplateService,
                                    @Autowired TranscriptService transcriptService) {
        this.sourceVideoStorageService = sourceVideoStorageService;
        this.processedService = processedService;
        this.templateService = templateService;
        this.taskManager = taskManager;
        this.audioTemplateService = audioTemplateService;
        this.transcriptService = transcriptService;
    }

    @Override
    public FilesContent readGeneratedVideo(UUID generatedVideosUuid) {
        ProcessedEntity entity = this.processedService.get(generatedVideosUuid).orElseThrow();
        UUID templateId = entity.getAudioTemplate().getTemplate().getId();
        return this.sourceVideoStorageService.readGeneratedVideo(templateId, generatedVideosUuid);
    }

    @Override
    public RequestsStatus createRequestToGenerate(RequestToGenerateVideo requestToGenerateVideo) {
        TemplateEntity templateEntity = this.templateService.getTemplate(requestToGenerateVideo.uuid())
                .orElseThrow();
        AudioTemplateEntity audioTemplate = this.audioTemplateService.getAudioTemplate(templateEntity)
                .orElseThrow();
        ProcessedEntity processedEntity = this.processedService.createNew(audioTemplate, requestToGenerateVideo.words());
        TranscriptEntity transcriptEntity = transcriptService.getTranscript(templateEntity)
                .orElseThrow();
        try {
            this.taskManager.createAudioGenerateTask(processedEntity, transcriptEntity);
            this.processedService.updateStatus(processedEntity, ProcessedService.STATUS_AUDIO_GEN_QUEUED);
            return new RequestsStatus(requestToGenerateVideo.id(),
                    processedEntity.getId(),
                    processedEntity.getStatus());
        } catch (JsonProcessingException e) {
            LOGGER.error("New status of {} is failed: {}",
                    requestToGenerateVideo.id(),
                    e.getMessage());
            this.processedService.updateStatus(processedEntity, e.getMessage());
            return new RequestsStatus(requestToGenerateVideo.id(),
                    processedEntity.getId(),
                    e.getMessage());
        }
    }

    @Override
    public void updateStatus(AudioGenerationStatus status) {
        if (status.status().equalsIgnoreCase(ProcessedService.STATUS_COMPLETE)) {
            ProcessedEntity processedEntity = this.processedService.get(status.processedId()).orElseThrow();
            TemplateEntity templateEntity = processedEntity.getAudioTemplate().getTemplate();
            TranscriptEntity transcriptEntity = transcriptService.getTranscript(templateEntity).orElseThrow();
            try {
                this.taskManager.createVideoGenerateTask(processedEntity, transcriptEntity);
                this.processedService.updateStatus(status.processedId(), ProcessedService.STATUS_VIDEO_GEN_QUEUED);
            } catch (JsonProcessingException e) {
                this.processedService.updateStatus(processedEntity, e.getMessage());
                LOGGER.error("New status of {} is {}: {}",
                        status.processedId(),
                        status.status(),
                        status.message());
            }
        } else {
            this.processedService.updateStatus(status.processedId(), status.status());
            LOGGER.info("New status of {} is {}: {}",
                    status.processedId(),
                    status.status(),
                    status.message());
        }
    }

    @Override
    public void updateStatus(VideoGenerationStatus status) {
        this.processedService.updateStatus(status.processedId(), status.status());
        LOGGER.info("New status of {} is {}: {}",
                status.processedId(),
                status.status(),
                status.message());
    }

    @Override
    public List<RequestsStatus> getStatuses(List<RequestStatus> requestStatuses) {
        Map<UUID, String> uuids = requestStatuses.stream()
                .collect(Collectors.toMap(RequestStatus::uuid, RequestStatus::id));
        List<ProcessedEntity> entities = this.processedService.getByIds(uuids.keySet());
        return entities.stream()
                .map(item -> new RequestsStatus(uuids.get(item.getId()),
                        item.getId(),
                        item.getStatus()))
                .toList();
    }

    @Override
    public List<RequestsStatus> createRequestsToGenerate(List<RequestToGenerateVideo> requestToGenerateVideo) {
        return requestToGenerateVideo.stream()
                .map(this::createRequestToGenerate)
                .toList();
    }
}
