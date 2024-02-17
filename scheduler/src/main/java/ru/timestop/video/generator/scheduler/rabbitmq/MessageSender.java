package ru.timestop.video.generator.scheduler.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Component
public class MessageSender {
    @Value("${rabbitmq.exchange}")
    private String topicExchange;
    @Value("${rabbitmq.routing.key}")
    private String routingKeyIn;
    @Value("${rabbitmq.expiration}")
    private String expiration;

    private final RabbitTemplate rabbitTemplate;

    public MessageSender(@Autowired RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(String message) {
        this.rabbitTemplate.convertAndSend(topicExchange,
                routingKeyIn,
                message, m -> {
                    m.getMessageProperties().setExpiration(expiration);
                    return m;
                });
    }
}