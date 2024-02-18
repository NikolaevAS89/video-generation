package ru.timestop.video.generator.server.facade.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.timestop.video.generator.server.facade.TranscriptFacade;
import ru.timestop.video.generator.server.template.TemplateService;
import ru.timestop.video.generator.server.template.entity.TemplateEntity;
import ru.timestop.video.generator.server.transcript.AudioTemplateService;
import ru.timestop.video.generator.server.transcript.TranscriptService;
import ru.timestop.video.generator.server.transcript.entity.TranscriptEntity;
import ru.timestop.video.generator.server.transcript.model.AudioTemplate;
import ru.timestop.video.generator.server.transcript.model.WordMetadata;

import java.util.List;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Service
public class TranscriptFacadeImpl implements TranscriptFacade {
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
    public UUID createAndSave(UUID template_uuid, List<WordMetadata> transcript) {
        TemplateEntity templateEntity = this.templateService.getTask(template_uuid);
        TranscriptEntity transcriptEntity = this.transcriptService.createAndSave(templateEntity, transcript);
        templateEntity.setStatus("Transcribed"); // TODO status as enum
        this.templateService.update(templateEntity);
        return transcriptEntity.getId();
    }

    @Override
    public List<WordMetadata> getTranscript(UUID template_uuid) {
        TemplateEntity templateEntity = this.templateService.getTask(template_uuid);
        return this.transcriptService.getTranscript(templateEntity);
    }

    @Override
    public void setChosen(UUID template_uuid, AudioTemplate chosen) {
        TemplateEntity templateEntity = this.templateService.getTask(template_uuid);
        this.audioTemplateService.createAndSave(templateEntity, chosen);
    }

    @Override
    public AudioTemplate getChosen(UUID template_uuid) {
        TemplateEntity templateEntity = this.templateService.getTask(template_uuid);
        return this.audioTemplateService.getAudioTemplate(templateEntity);
    }
}
