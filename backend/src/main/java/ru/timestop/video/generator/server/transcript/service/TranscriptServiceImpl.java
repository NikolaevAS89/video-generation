package ru.timestop.video.generator.server.transcript.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.timestop.video.generator.server.transcript.TranscriptService;
import ru.timestop.video.generator.server.transcript.model.WordMetadata;
import ru.timestop.video.generator.server.template.entity.TemplateEntity;
import ru.timestop.video.generator.server.transcript.entity.TranscriptEntity;
import ru.timestop.video.generator.server.transcript.repository.TranscriptRepository;

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
                .setTemplate(templateEntity);
        return transcriptRepository.save(transcriptEntity);
    }

    @Override
    public List<WordMetadata> getTranscript(TemplateEntity templateEntity) {
        return this.transcriptRepository.findByTemplate(templateEntity)
                .orElse(EMPTY_ENTITY)
                .getTranscript();
    }

    @Override
    public void deleteByTemplate(TemplateEntity template) {
        this.transcriptRepository.deleteByTemplate(template);
    }
}
