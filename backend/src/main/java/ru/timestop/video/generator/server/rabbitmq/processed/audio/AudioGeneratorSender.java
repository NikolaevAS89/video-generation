package ru.timestop.video.generator.server.rabbitmq.processed.audio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.timestop.video.generator.server.rabbitmq.processed.audio.model.AudioGenerationTask;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Component
public class AudioGeneratorSender {

    @Value("${rabbitmq.audio.generation.exchange}")
    private String topicExchangeName;
    @Value("${rabbitmq.audio.generation.in.routing.key}")
    private String routingKeyIn;

    private final ObjectMapper objectMapper;

    private final RabbitTemplate rabbitTemplate;


    public AudioGeneratorSender(@Autowired ObjectMapper objectMapper,
                                @Autowired RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void send(AudioGenerationTask audioGenerationTask) throws JsonProcessingException {
        String message = objectMapper.writeValueAsString(audioGenerationTask);
        this.rabbitTemplate.convertAndSend(topicExchangeName, routingKeyIn, message);
    }
}