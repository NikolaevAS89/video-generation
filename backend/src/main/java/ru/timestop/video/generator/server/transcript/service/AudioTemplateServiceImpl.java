package ru.timestop.video.generator.server.transcript.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.timestop.video.generator.server.template.entity.TemplateEntity;
import ru.timestop.video.generator.server.transcript.AudioTemplateService;
import ru.timestop.video.generator.server.transcript.entity.AudioTemplateEntity;
import ru.timestop.video.generator.server.transcript.model.AudioTemplate;
import ru.timestop.video.generator.server.transcript.repository.AudioTemplateRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Service
public class AudioTemplateServiceImpl implements AudioTemplateService {
    private static final AudioTemplateEntity EMPTY_ENTITY = new AudioTemplateEntity()
            .setChosen(Collections.emptyList())
            .setMapping(Collections.emptyMap());
    private final AudioTemplateRepository audioTemplateRepository;

    public AudioTemplateServiceImpl(@Autowired AudioTemplateRepository audioTemplateRepository) {
        this.audioTemplateRepository = audioTemplateRepository;
    }


    @Override
    public AudioTemplateEntity createAndSave(TemplateEntity template, AudioTemplate audioTemplate) {
        AudioTemplateEntity transcriptEntity = new AudioTemplateEntity()
                .setCreation(Timestamp.valueOf(LocalDateTime.now()))
                .setTemplate(template)
                .setChosen(audioTemplate.choosed())
                .setMapping(audioTemplate.mapping());
        return audioTemplateRepository.save(transcriptEntity);
    }

    @Override
    public AudioTemplate getAudioTemplate(TemplateEntity template) {
        AudioTemplateEntity entity = this.audioTemplateRepository.findByTemplate(template)
                .orElse(EMPTY_ENTITY);
        return new AudioTemplate(entity.getChosen(), entity.getMapping());
    }

    @Override
    public void deleteByTemplate(TemplateEntity template) {
        this.audioTemplateRepository.deleteByTemplate(template);
    }
}
