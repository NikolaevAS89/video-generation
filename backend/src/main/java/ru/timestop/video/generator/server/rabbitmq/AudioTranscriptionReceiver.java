package ru.timestop.video.generator.server.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.timestop.video.generator.server.facade.TranscriptFacade;
import ru.timestop.video.generator.server.template.TemplateService;
import ru.timestop.video.generator.server.template.entity.TemplateEntity;
import ru.timestop.video.generator.server.transcript.model.AudioTranscription;

import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Service
public class AudioTranscriptionReceiver implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudioTranscriptionReceiver.class);
    private final ObjectMapper objectMapper;
    private final TranscriptFacade transcriptFacade;
    private final TemplateService templateService;

    public AudioTranscriptionReceiver(@Autowired ObjectMapper objectMapper,
                                      @Autowired TranscriptFacade transcriptFacade,
                                      @Autowired TemplateService templateService) {
        this.objectMapper = objectMapper;
        this.transcriptFacade = transcriptFacade;
        this.templateService = templateService;
    }

    @Override
    public void onMessage(Message message) {
        try {
            byte[] body = message.getBody();
            AudioTranscription audioTranscriptions = this.objectMapper.readValue(body, AudioTranscription.class);
            if (!audioTranscriptions.status().equalsIgnoreCase("successes")) {
                TemplateEntity task = this.templateService.getTask(audioTranscriptions.uuid());
                task.setStatus(audioTranscriptions.message());
                this.templateService.update(task);
            } else {
                UUID uuid = this.transcriptFacade.createAndSave(audioTranscriptions.uuid(), audioTranscriptions.words());
                LOGGER.info("New transcript {} was created", uuid);
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }
}
