package ru.timestop.video.generator.server.processed.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.timestop.video.generator.server.processed.ProcessedService;
import ru.timestop.video.generator.server.processed.entity.ProcessedEntity;
import ru.timestop.video.generator.server.processed.repository.ProcessedRepository;
import ru.timestop.video.generator.server.audiotemplate.entity.AudioTemplateEntity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Service
public class ProcessedServiceImpl implements ProcessedService {

    private final ProcessedRepository processedRepository;

    public ProcessedServiceImpl(@Autowired ProcessedRepository processedRepository) {
        this.processedRepository = processedRepository;
    }

    @Override
    public ProcessedEntity createNew(AudioTemplateEntity audioTemplateEntity, Map<String, String> words) {
        ProcessedEntity processedEntity = new ProcessedEntity()
                .setStatus(STATUS_CREATED)
                .setCreation(Timestamp.valueOf(LocalDateTime.now()))
                .setAudioTemplate(audioTemplateEntity)
                .setWords(words);
        return this.processedRepository.saveAndFlush(processedEntity);
    }

    @Override
    public ProcessedEntity updateStatus(UUID processedId, String newStatus) {
        ProcessedEntity processedEntity = this.processedRepository.findById(processedId)
                .orElseThrow();
        processedEntity.setStatus(newStatus);
        return this.processedRepository.saveAndFlush(processedEntity);
    }

    @Override
    public ProcessedEntity updateStatus(ProcessedEntity processedEntity, String newStatus) {
        processedEntity.setStatus(newStatus);
        return this.processedRepository.saveAndFlush(processedEntity);
    }

    @Override
    public void deleteAllByAudioTemplate(AudioTemplateEntity audioTemplateEntity) {
        this.processedRepository.deleteByAudioTemplate(audioTemplateEntity);
    }

    @Override
    public Optional<ProcessedEntity> get(UUID processedId) {
        return this.processedRepository.findById(processedId);
    }

    @Override
    public List<ProcessedEntity> getAll() {
        return this.processedRepository.findAll();
    }

    @Override
    public List<ProcessedEntity> getByIds(Set<UUID> processedIds) {
        return this.processedRepository.findAllById(processedIds);
    }

    @Override
    public void delete(AudioTemplateEntity audioTemplateEntity) {
        this.processedRepository.deleteByAudioTemplate(audioTemplateEntity);
    }
}
