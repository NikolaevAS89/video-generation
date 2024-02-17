package ru.timestop.video.generator.server.rabbitmq;

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
public class AudioTranscriptionQueueConfigure {
    @Value("${rabbitmq.transcription.in.queue}")
    private String queueInName;
    @Value("${rabbitmq.transcription.out.queue}")
    private String queueOutName;
    @Value("${rabbitmq.transcription.exchange}")
    private String topicExchangeName;
    @Value("${rabbitmq.transcription.in.routing.key}")
    private String routingKeyIn;
    @Value("${rabbitmq.transcription.out.routing.key}")
    private String routingKeyOut;

    @Bean(name = "QUEUE_IN_NAME")
    public Queue queueIn() {
        return new Queue(queueInName, true);
    }

    @Bean(name = "QUEUE_OUT_NAME")
    public Queue queueOut() {
        return new Queue(queueOutName, true);
    }

    @Bean(name = "TOPIC_EXCHANGE_NAME")
    public DirectExchange exchange() {
        return new DirectExchange(topicExchangeName,
                true,
                false);
    }


    @Bean(name = "ROUTING_KEY_IN")
    public Binding bindingIn(@Qualifier("QUEUE_IN_NAME") Queue queue,
                             @Qualifier("TOPIC_EXCHANGE_NAME") DirectExchange exchange) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(routingKeyIn);
    }

    @Bean(name = "ROUTING_KEY_OUT")
    public Binding bindingOut(@Qualifier("QUEUE_OUT_NAME") Queue queue,
                              @Qualifier("TOPIC_EXCHANGE_NAME") DirectExchange exchange) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(routingKeyOut);
    }

    @Bean
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                                    AudioTranscriptionReceiver receiver) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueOutName);
        container.setMessageListener(receiver);
        return container;
    }
}
