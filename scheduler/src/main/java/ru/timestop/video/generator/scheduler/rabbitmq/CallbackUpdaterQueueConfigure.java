package ru.timestop.video.generator.scheduler.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Configuration
public class CallbackUpdaterQueueConfigure {
    @Value("${rabbitmq.queue}")
    private String queueName;
    @Value("${rabbitmq.exchange}")
    private String topicExchange;
    @Value("${rabbitmq.routing.key}")
    private String routingKeyIn;

    @Bean
    public Queue queueIn() {
        return new Queue(queueName, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(topicExchange,
                true,
                false);
    }


    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(routingKeyIn);
    }
}
