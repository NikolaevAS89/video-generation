package ru.timestop.video.generator.server.rabbitmq.processed.video;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.timestop.video.generator.server.rabbitmq.processed.video.model.VideoGenerationTask;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Component
public class VideoGeneratorSender {
    @Value("${rabbitmq.video.generation.exchange}")
    private String topicExchangeName;
    @Value("${rabbitmq.video.generation.in.routing.key}")
    private String routingKeyIn;

    private final ObjectMapper objectMapper;

    private final RabbitTemplate rabbitTemplate;

    public VideoGeneratorSender(@Autowired ObjectMapper objectMapper,
                                @Autowired RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void send(VideoGenerationTask videoGenerationTask) throws JsonProcessingException {
        String message = objectMapper.writeValueAsString(videoGenerationTask);
        this.rabbitTemplate.convertAndSend(topicExchangeName,
                routingKeyIn,
                message);
    }
}