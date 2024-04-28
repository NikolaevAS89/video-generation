package ru.timestop.video.generator.server.rabbitmq.transcript;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Component
public class AudioTranscriptionSender {
    @Value("${rabbitmq.transcription.exchange}")
    private String topicExchangeName;
    @Value("${rabbitmq.transcription.in.routing.key}")
    private String routingKeyIn;

    private final RabbitTemplate rabbitTemplate;

    public AudioTranscriptionSender(@Autowired RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(UUID uuid) {
        this.rabbitTemplate.convertAndSend(topicExchangeName,
                routingKeyIn,
                uuid.toString());
    }
}