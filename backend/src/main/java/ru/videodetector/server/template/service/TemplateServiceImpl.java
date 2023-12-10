package ru.videodetector.server.template.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.videodetector.server.rabbitmq.AudioTranscriptionSender;
import ru.videodetector.server.storage.SourceVideoStorageService;
import ru.videodetector.server.template.TemplateService;
import ru.videodetector.server.template.entity.TemplateEntity;
import ru.videodetector.server.template.repository.TemplateRepository;
import ru.videodetector.server.transcript.TranscriptService;

import java.io.InputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Service
public class TemplateServiceImpl implements TemplateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateServiceImpl.class);

    private final TemplateRepository templateRepository;
    private final SourceVideoStorageService sourceVideoStorageService;

    private final TranscriptService transcriptService;
    private final AudioTranscriptionSender audioTranscriptionSender;

    public TemplateServiceImpl(@Autowired TemplateRepository templateRepository,
                               @Autowired SourceVideoStorageService sourceVideoStorageService,
                               @Autowired TranscriptService transcriptService,
                               @Autowired AudioTranscriptionSender audioTranscriptionSender) {
        this.templateRepository = templateRepository;
        this.sourceVideoStorageService = sourceVideoStorageService;
        this.transcriptService = transcriptService;
        this.audioTranscriptionSender = audioTranscriptionSender;
    }

    @Override
    public TemplateEntity createTask(String filename, InputStream stream) {
        TemplateEntity templateEntity = new TemplateEntity().setName(filename)
                .setStatus("Start loading...")
                .setCreation(Timestamp.valueOf(LocalDateTime.now()));
        try {
            templateEntity = this.templateRepository.save(templateEntity);
            this.sourceVideoStorageService.saveSourceVideo(templateEntity.getId(), stream);
            templateEntity.setStatus("Loading was complited.");
            this.audioTranscriptionSender.send(templateEntity.getId());
            return this.templateRepository.saveAndFlush(templateEntity);
        } catch (Exception e) {
            this.sourceVideoStorageService.deleteFolder(templateEntity.getId());
            throw new RuntimeException("File not loaded " + filename, e);
        }
    }

    @Override
    public TemplateEntity update(TemplateEntity templateEntity) {
        return this.templateRepository.save(templateEntity);
    }

    @Transactional
    @Override
    public void delete(UUID uuid) {
        TemplateEntity templateEntity = this.getTask(uuid);
        this.transcriptService.deleteByTemplate(templateEntity);
        this.templateRepository.delete(templateEntity);
        this.sourceVideoStorageService.deleteFolder(uuid);
    }

    @Override
    public TemplateEntity getTask(UUID uuid) {
        return this.templateRepository.getReferenceById(uuid);
    }

    @Override
    public List<TemplateEntity> getAllTasks() {
        return this.templateRepository.findAll();
    }
}
