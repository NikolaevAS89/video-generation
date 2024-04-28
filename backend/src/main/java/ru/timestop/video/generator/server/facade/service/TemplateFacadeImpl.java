package ru.timestop.video.generator.server.facade.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.timestop.video.generator.server.audiotemplate.AudioTemplateService;
import ru.timestop.video.generator.server.facade.TemplateFacade;
import ru.timestop.video.generator.server.processed.ProcessedService;
import ru.timestop.video.generator.server.storage.SourceVideoStorageService;
import ru.timestop.video.generator.server.template.TemplateService;
import ru.timestop.video.generator.server.template.entity.TemplateEntity;
import ru.timestop.video.generator.server.transcript.TranscriptService;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Service
public class TemplateFacadeImpl implements TemplateFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateFacadeImpl.class);
    private final TemplateService templateService;
    private final SourceVideoStorageService sourceVideoStorageService;
    private final TranscriptService transcriptService;
    private final AudioTemplateService audioTemplateService;

    private final ProcessedService processedService;

    public TemplateFacadeImpl(@Autowired TemplateService templateService,
                              @Autowired SourceVideoStorageService sourceVideoStorageService,
                              @Autowired TranscriptService transcriptService,
                              @Autowired AudioTemplateService audioTemplateService,
                              @Autowired ProcessedService processedService) {
        this.templateService = templateService;
        this.sourceVideoStorageService = sourceVideoStorageService;
        this.transcriptService = transcriptService;
        this.audioTemplateService = audioTemplateService;
        this.processedService = processedService;
    }

    @Override
    public TemplateEntity createTemplate(String filename, InputStream stream) {
        return this.templateService.createTemplate(filename, stream);
    }

    @Override
    public TemplateEntity getTemplate(UUID uuid) {
        return this.templateService.getTemplate(uuid).orElseThrow();
    }

    @Override
    public List<TemplateEntity> getTemplates() {
        return this.templateService.getTemplates();
    }

    @Transactional
    @Override
    public void delete(UUID uuid) {
        TemplateEntity templateEntity = this.templateService.getTemplate(uuid).orElseThrow();
        this.audioTemplateService.getAudioTemplate(templateEntity)
                .ifPresent(processedService::deleteAllByAudioTemplate);
        this.audioTemplateService.deleteByTemplate(templateEntity);
        this.transcriptService.deleteByTemplate(templateEntity);
        this.templateService.delete(templateEntity);
        this.sourceVideoStorageService.deleteFolder(uuid);
    }
}
