package ru.videodetector.server.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Component
public class AudioTranscriptionSender {

    private final RabbitTemplate rabbitTemplate;

    public AudioTranscriptionSender(@Autowired RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(UUID uuid) {
        this.rabbitTemplate.convertAndSend(AudioTranscriptionQueueConfigure.TOPIC_EXCHANGE_NAME,
                AudioTranscriptionQueueConfigure.ROUTING_KEY_IN,
                uuid.toString());
    }
}