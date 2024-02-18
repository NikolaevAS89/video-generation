package ru.timestop.video.generator.server.processed.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.timestop.video.generator.server.processed.GeneratedVideoStorageService;
import ru.timestop.video.generator.server.processed.entity.ProcessedEntity;
import ru.timestop.video.generator.server.processed.model.request.RequestStatus;
import ru.timestop.video.generator.server.processed.model.request.RequestToGenerateVideo;
import ru.timestop.video.generator.server.processed.model.response.RequestsStatus;
import ru.timestop.video.generator.server.processed.repository.ProcessedRepository;
import ru.timestop.video.generator.server.storage.SourceVideoStorageService;
import ru.timestop.video.generator.server.storage.model.FilesContent;
import ru.timestop.video.generator.server.template.TemplateService;
import ru.timestop.video.generator.server.template.entity.TemplateEntity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Service
public class GeneratedVideoStorageServiceImpl implements GeneratedVideoStorageService {
    private final SourceVideoStorageService sourceVideoStorageService;
    private final ProcessedRepository processedRepository;

    private final TemplateService templateService;

    public GeneratedVideoStorageServiceImpl(@Autowired SourceVideoStorageService sourceVideoStorageService,
                                            @Autowired ProcessedRepository processedRepository,
                                            @Autowired TemplateService templateService) {
        this.sourceVideoStorageService = sourceVideoStorageService;
        this.processedRepository = processedRepository;
        this.templateService = templateService;
    }

    @Override
    public FilesContent readGeneratedVideo(UUID generatedVideosUuid) {
        ProcessedEntity entity = this.processedRepository.findById(generatedVideosUuid).orElseThrow();
        return this.sourceVideoStorageService.readGeneratedVideo(entity.getTemplate().getId(), generatedVideosUuid);
    }

    @Override
    public RequestsStatus getStatus(RequestStatus requestStatus) {
        ProcessedEntity entity = this.processedRepository
                .findById(requestStatus.uuid())
                .orElseThrow();
        return new RequestsStatus(requestStatus.id(),
                entity.getId(),
                entity.getStatus());

    }

    @Override
    public RequestsStatus createRequestToGenerate(RequestToGenerateVideo requestToGenerateVideo) {
        TemplateEntity templateEntity = this.templateService.getTask(requestToGenerateVideo.uuid());
        ProcessedEntity processedEntity = new ProcessedEntity()
                .setStatus("Send to the generator")
                .setCreation(Timestamp.valueOf(LocalDateTime.now()))
                .setTemplate(templateEntity)
                .setWords(requestToGenerateVideo.words());
        processedEntity = this.processedRepository.saveAndFlush(processedEntity);
        return new RequestsStatus(requestToGenerateVideo.id(),
                processedEntity.getId(),
                processedEntity.getStatus());
    }

    @Override
    public List<RequestsStatus> getStatuses(List<RequestStatus> requestStatuses) {
        Map<UUID, String> uuids = requestStatuses.stream()
                .collect(Collectors.toMap(RequestStatus::uuid, RequestStatus::id));
        List<ProcessedEntity> entities = this.processedRepository.findAllById(uuids.keySet());
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
