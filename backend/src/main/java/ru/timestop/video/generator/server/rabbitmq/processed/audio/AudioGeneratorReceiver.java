package ru.timestop.video.generator.server.rabbitmq.processed.audio;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.timestop.video.generator.server.facade.VideoCompilerService;
import ru.timestop.video.generator.server.rabbitmq.transcript.AudioTranscriptionReceiver;
import ru.timestop.video.generator.server.rabbitmq.processed.audio.model.AudioGenerationStatus;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Component
public class AudioGeneratorReceiver implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudioTranscriptionReceiver.class);
    private final ObjectMapper objectMapper;
    private final VideoCompilerService videoCompilerService;

    public AudioGeneratorReceiver(@Autowired ObjectMapper objectMapper,
                                  @Autowired VideoCompilerService videoCompilerService) {
        this.objectMapper = objectMapper;
        this.videoCompilerService = videoCompilerService;
    }

    @Override
    public void onMessage(Message message) {
        try {
            byte[] body = message.getBody();
            AudioGenerationStatus status = this.objectMapper.readValue(body, AudioGenerationStatus.class);
            this.videoCompilerService.updateStatus(status);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }
}