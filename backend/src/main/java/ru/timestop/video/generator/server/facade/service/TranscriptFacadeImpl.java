package ru.timestop.video.generator.server.facade.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.timestop.video.generator.server.audiotemplate.AudioTemplateService;
import ru.timestop.video.generator.server.audiotemplate.entity.AudioTemplateEntity;
import ru.timestop.video.generator.server.facade.TranscriptFacade;
import ru.timestop.video.generator.server.facade.model.AudioTemplate;
import ru.timestop.video.generator.server.template.TemplateService;
import ru.timestop.video.generator.server.template.entity.TemplateEntity;
import ru.timestop.video.generator.server.transcript.TranscriptService;
import ru.timestop.video.generator.server.transcript.entity.TranscriptEntity;
import ru.timestop.video.generator.server.transcript.model.WordMetadata;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Service
public class TranscriptFacadeImpl implements TranscriptFacade {
    AudioTemplateEntity EMPTY_AUDIO_TEMPLATE_ENTITY = new AudioTemplateEntity()
            .setChosen(Collections.emptyList())
            .setMapping(Collections.emptyMap());
    private final TemplateService templateService;
    private final TranscriptService transcriptService;
    private final AudioTemplateService audioTemplateService;

    public TranscriptFacadeImpl(@Autowired TemplateService templateService,
                                @Autowired TranscriptService transcriptService,
                                @Autowired AudioTemplateService audioTemplateService) {
        this.templateService = templateService;
        this.transcriptService = transcriptService;
        this.audioTemplateService = audioTemplateService;
    }

    @Override
    public UUID createAndSave(UUID templateId, List<WordMetadata> transcript) {
        TemplateEntity templateEntity = this.templateService.getTemplate(templateId)
                .orElseThrow();
        TranscriptEntity transcriptEntity = this.transcriptService.createAndSave(templateEntity, transcript);
        templateEntity.setStatus("Transcribed");
        this.templateService.update(templateEntity);
        return transcriptEntity.getId();
    }

    @Override
    public List<WordMetadata> getTranscript(UUID templateId) {
        TemplateEntity templateEntity = this.templateService.getTemplate(templateId)
                .orElseThrow();
        return this.transcriptService.getTranscript(templateEntity)
                .orElseThrow()
                .getTranscript();
    }

    @Override
    public void setChosen(UUID templateId, AudioTemplate audioTemplate) {
        TemplateEntity templateEntity = this.templateService.getTemplate(templateId)
                .orElseThrow();
        this.audioTemplateService.createAndSave(templateEntity,
                audioTemplate.choosed(),
                audioTemplate.mapping());
    }

    @Override
    public AudioTemplate getChosen(UUID templateId) {
        TemplateEntity templateEntity = this.templateService.getTemplate(templateId)
                .orElseThrow();
        AudioTemplateEntity audioTemplateEntity = this.audioTemplateService.getAudioTemplate(templateEntity)
                .orElse(EMPTY_AUDIO_TEMPLATE_ENTITY);
        return new AudioTemplate(audioTemplateEntity.getChosen(),
                audioTemplateEntity.getMapping());
    }
}
