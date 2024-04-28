package ru.timestop.video.generator.server.audiotemplate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.timestop.video.generator.server.audiotemplate.AudioTemplateService;
import ru.timestop.video.generator.server.audiotemplate.entity.AudioTemplateEntity;
import ru.timestop.video.generator.server.audiotemplate.repository.AudioTemplateRepository;
import ru.timestop.video.generator.server.template.entity.TemplateEntity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Service
public class AudioTemplateServiceImpl implements AudioTemplateService {
    private final AudioTemplateRepository audioTemplateRepository;

    public AudioTemplateServiceImpl(@Autowired AudioTemplateRepository audioTemplateRepository) {
        this.audioTemplateRepository = audioTemplateRepository;
    }


    @Override
    public AudioTemplateEntity createAndSave(TemplateEntity template, List<Integer> chosen, Map<String, Integer> mapping) {
        AudioTemplateEntity transcriptEntity = new AudioTemplateEntity()
                .setCreation(Timestamp.valueOf(LocalDateTime.now()))
                .setTemplate(template)
                .setChosen(chosen)
                .setMapping(mapping);
        return this.audioTemplateRepository.save(transcriptEntity);
    }

    @Override
    public Optional<AudioTemplateEntity> getAudioTemplate(TemplateEntity template) {
        return this.audioTemplateRepository.findByTemplate(template);
    }

    @Override
    public void deleteByTemplate(TemplateEntity template) {
        this.audioTemplateRepository.deleteByTemplate(template);
    }
}
