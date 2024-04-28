package ru.timestop.video.generator.server.template.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.timestop.video.generator.server.rabbitmq.transcript.AudioTranscriptionSender;
import ru.timestop.video.generator.server.storage.SourceVideoStorageService;
import ru.timestop.video.generator.server.template.TemplateService;
import ru.timestop.video.generator.server.template.entity.TemplateEntity;
import ru.timestop.video.generator.server.template.repository.TemplateRepository;
import ru.timestop.video.generator.server.audiotemplate.AudioTemplateService;
import ru.timestop.video.generator.server.transcript.TranscriptService;

import java.io.InputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Service
public class TemplateServiceImpl implements TemplateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateServiceImpl.class);
    private final TemplateRepository templateRepository;
    private final SourceVideoStorageService sourceVideoStorageService;
    private final AudioTranscriptionSender audioTranscriptionSender;

    public TemplateServiceImpl(@Autowired TemplateRepository templateRepository,
                               @Autowired SourceVideoStorageService sourceVideoStorageService,
                               @Autowired AudioTranscriptionSender audioTranscriptionSender) {
        this.templateRepository = templateRepository;
        this.sourceVideoStorageService = sourceVideoStorageService;
        this.audioTranscriptionSender = audioTranscriptionSender;
    }

    @Override
    public TemplateEntity createTemplate(String filename, InputStream stream) {
        TemplateEntity templateEntity = new TemplateEntity().setName(filename)
                .setStatus("Start loading...")
                .setCreation(Timestamp.valueOf(LocalDateTime.now()));
        try {
            templateEntity = this.templateRepository.save(templateEntity);
            this.sourceVideoStorageService.saveSourceVideo(templateEntity.getId(), stream);
            templateEntity.setStatus("Loading was completed.");
            this.audioTranscriptionSender.send(templateEntity.getId());
            return this.templateRepository.saveAndFlush(templateEntity);
        } catch (Exception e) {
            this.sourceVideoStorageService.deleteFolder(templateEntity.getId());
            templateEntity.setStatus("Error: " + e.getMessage());
            this.templateRepository.save(templateEntity);
            throw new RuntimeException("File not loaded " + filename, e);
        }
    }

    @Override
    public TemplateEntity update(TemplateEntity templateEntity) {
        return this.templateRepository.save(templateEntity);
    }

    @Override
    public void delete(TemplateEntity templateEntity) {
        this.templateRepository.delete(templateEntity);
    }


    @Override
    public Optional<TemplateEntity> getTemplate(UUID uuid) {
        return this.templateRepository.findById(uuid);
    }


    @Override
    public List<TemplateEntity> getTemplates() {
        return this.templateRepository.findAll();
    }
}
