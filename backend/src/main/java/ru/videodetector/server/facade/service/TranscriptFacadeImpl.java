package ru.videodetector.server.facade.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.videodetector.server.facade.TranscriptFacade;
import ru.videodetector.server.template.TemplateService;
import ru.videodetector.server.template.entity.TemplateEntity;
import ru.videodetector.server.transcript.TranscriptService;
import ru.videodetector.server.transcript.model.WordMetadata;

import java.util.List;
import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Service
public class TranscriptFacadeImpl implements TranscriptFacade {
    private final TemplateService templateService;
    private final TranscriptService transcriptService;

    public TranscriptFacadeImpl(@Autowired TemplateService templateService,
                                @Autowired TranscriptService transcriptService) {
        this.templateService = templateService;
        this.transcriptService = transcriptService;
    }

    @Override
    public UUID createAndSave(UUID task_id, List<WordMetadata> transcript) {
        TemplateEntity templateEntity = this.templateService.getTask(task_id);
        return this.transcriptService.createAndSave(templateEntity, transcript).getId();
    }

    @Override
    public List<WordMetadata> getTranscript(UUID task_id) {
        TemplateEntity templateEntity = this.templateService.getTask(task_id);
        return this.transcriptService.getTranscript(templateEntity);
    }

    @Override
    public void setChosen(UUID task_id, List<Integer> chosen) {
        TemplateEntity templateEntity = this.templateService.getTask(task_id);
        this.transcriptService.setChosen(templateEntity, chosen);
    }

    @Override
    public List<Integer> getChosen(UUID task_id) {
        TemplateEntity templateEntity = this.templateService.getTask(task_id);
        return this.transcriptService.getChosen(templateEntity);
    }
}
