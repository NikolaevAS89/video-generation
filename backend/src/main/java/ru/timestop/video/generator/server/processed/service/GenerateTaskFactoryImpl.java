package ru.timestop.video.generator.server.processed.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.timestop.video.generator.server.processed.GenerateTaskFactory;
import ru.timestop.video.generator.server.processed.entity.ProcessedEntity;
import ru.timestop.video.generator.server.rabbitmq.processed.audio.model.AudioGenerationTask;
import ru.timestop.video.generator.server.rabbitmq.processed.video.model.VideoGenerationTask;
import ru.timestop.video.generator.server.rabbitmq.processed.audio.AudioGeneratorSender;
import ru.timestop.video.generator.server.rabbitmq.processed.video.VideoGeneratorSender;
import ru.timestop.video.generator.server.transcript.entity.TranscriptEntity;

import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Service
public class GenerateTaskFactoryImpl implements GenerateTaskFactory {

    private final AudioGeneratorSender audioGeneratorSender;

    private final VideoGeneratorSender videoGeneratorSender;


    public GenerateTaskFactoryImpl(@Autowired AudioGeneratorSender audioGeneratorSender,
                                   @Autowired VideoGeneratorSender videoGeneratorSender) {
        this.audioGeneratorSender = audioGeneratorSender;
        this.videoGeneratorSender = videoGeneratorSender;
    }

    @Override
    public void createAudioGenerateTask(ProcessedEntity processedEntity, TranscriptEntity transcriptEntity) throws JsonProcessingException {
        UUID templateId = processedEntity.getAudioTemplate().getTemplate().getId();
        AudioGenerationTask task = new AudioGenerationTask(
                templateId,
                processedEntity.getId(),
                processedEntity.getAudioTemplate().getChosen(),
                processedEntity.getAudioTemplate().getMapping(),
                processedEntity.getWords(),
                transcriptEntity.getTranscript()
        );
        this.audioGeneratorSender.send(task);
    }

    @Override
    public void createVideoGenerateTask(ProcessedEntity processedEntity, TranscriptEntity transcriptEntity) throws JsonProcessingException {
        UUID templateId = processedEntity.getAudioTemplate().getTemplate().getId();
        VideoGenerationTask task = new VideoGenerationTask(
                templateId,
                processedEntity.getId(),
                processedEntity.getAudioTemplate().getChosen(),
                processedEntity.getAudioTemplate().getMapping(),
                processedEntity.getWords(),
                transcriptEntity.getTranscript()
        );
        this.videoGeneratorSender.send(task);
    }
}
