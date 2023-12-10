package ru.videodetector.server.transcript.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.videodetector.server.template.entity.TemplateEntity;
import ru.videodetector.server.transcript.TranscriptService;
import ru.videodetector.server.transcript.entity.TranscriptEntity;
import ru.videodetector.server.transcript.model.WordMetadata;
import ru.videodetector.server.transcript.repository.TranscriptRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Service
public class TranscriptServiceImpl implements TranscriptService {
    private static final TranscriptEntity EMPTY_ENTITY = new TranscriptEntity().setTranscript(Collections.emptyList());
    private final TranscriptRepository transcriptRepository;

    public TranscriptServiceImpl(@Autowired TranscriptRepository transcriptRepository) {
        this.transcriptRepository = transcriptRepository;
    }

    @Override
    public TranscriptEntity createAndSave(TemplateEntity templateEntity, List<WordMetadata> transcript) {
        TranscriptEntity transcriptEntity = new TranscriptEntity().setCreation(Timestamp.valueOf(LocalDateTime.now()))
                .setTranscript(transcript)
                .setTemplate(templateEntity)
                .setChosen(Collections.emptyList());
        return transcriptRepository.save(transcriptEntity);
    }

    @Override
    public List<WordMetadata> getTranscript(TemplateEntity templateEntity) {
        return this.transcriptRepository.findByTemplate(templateEntity)
                .orElse(EMPTY_ENTITY)
                .getTranscript();
    }

    @Override
    public void setChosen(TemplateEntity templateEntity, List<Integer> chosen) {
        this.transcriptRepository.findByTemplate(templateEntity)
                .ifPresent(transcriptEntity -> {
                    transcriptEntity.setChosen(chosen);
                    this.transcriptRepository.save(transcriptEntity);
                });
    }

    @Override
    public List<Integer> getChosen(TemplateEntity templateEntity) {
        return this.transcriptRepository.findByTemplate(templateEntity).orElse(EMPTY_ENTITY).getChosen();
    }

    @Override
    public void deleteByTemplate(TemplateEntity template) {
        this.transcriptRepository.deleteByTemplate(template);
    }
}
