package ru.timestop.video.generator.server.rabbitmq.processed.audio;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Configuration
public class AudioGeneratorQueueConfigure {
    @Value("${rabbitmq.audio.generation.in.queue}")
    private String queueInName;
    @Value("${rabbitmq.audio.generation.out.queue}")
    private String queueOutName;
    @Value("${rabbitmq.audio.generation.exchange}")
    private String topicExchangeName;
    @Value("${rabbitmq.audio.generation.in.routing.key}")
    private String routingKeyIn;
    @Value("${rabbitmq.audio.generation.out.routing.key}")
    private String routingKeyOut;

    @Bean(name = "AG_QUEUE_IN_NAME")
    public Queue queueIn() {
        return new Queue(queueInName, true);
    }

    @Bean(name = "AG_QUEUE_OUT_NAME")
    public Queue queueOut() {
        return new Queue(queueOutName, true);
    }

    @Bean(name = "AG_TOPIC_EXCHANGE_NAME")
    public DirectExchange exchange() {
        return new DirectExchange(topicExchangeName,
                true,
                false);
    }

    @Bean(name = "AG_ROUTING_KEY_IN")
    public Binding bindingIn(@Qualifier("AG_QUEUE_IN_NAME") Queue queue,
                             @Qualifier("AG_TOPIC_EXCHANGE_NAME") DirectExchange exchange) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(routingKeyIn);
    }

    @Bean(name = "AG_ROUTING_KEY_OUT")
    public Binding bindingOut(@Qualifier("AG_QUEUE_OUT_NAME") Queue queue,
                              @Qualifier("AG_TOPIC_EXCHANGE_NAME") DirectExchange exchange) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(routingKeyOut);
    }

    @Bean(name = "AudioGeneratorMessageListener")
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                                    AudioGeneratorReceiver receiver) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueOutName);
        container.setMessageListener(receiver);
        return container;
    }
}
