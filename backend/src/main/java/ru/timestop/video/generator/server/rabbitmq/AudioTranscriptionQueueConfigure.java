package ru.timestop.video.generator.server.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
@Configuration
public class AudioTranscriptionQueueConfigure {
    static final String QUEUE_IN_NAME = "audio.transcription.queue.in";
    static final String QUEUE_OUT_NAME = "audio.transcription.queue.out";
    static final String TOPIC_EXCHANGE_NAME = "audio.transcription.exchange";
    static final String ROUTING_KEY_IN = "audio.transcript.in";
    static final String ROUTING_KEY_OUT = "audio.transcript.out";

    @Bean(name = QUEUE_IN_NAME)
    public Queue queueIn() {
        return new Queue(QUEUE_IN_NAME, true);
    }

    @Bean(name = QUEUE_OUT_NAME)
    public Queue queueOut() {
        return new Queue(QUEUE_OUT_NAME, true);
    }

    @Bean(name = TOPIC_EXCHANGE_NAME)
    public DirectExchange exchange() {
        return new DirectExchange(TOPIC_EXCHANGE_NAME,
                true,
                false);
    }


    @Bean(name = ROUTING_KEY_IN)
    public Binding bindingIn(@Qualifier(QUEUE_IN_NAME) Queue queue,
                             @Qualifier(TOPIC_EXCHANGE_NAME) DirectExchange exchange) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(ROUTING_KEY_IN);
    }

    @Bean(name = ROUTING_KEY_OUT)
    public Binding bindingOut(@Qualifier(QUEUE_OUT_NAME) Queue queue,
                              @Qualifier(TOPIC_EXCHANGE_NAME) DirectExchange exchange) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(ROUTING_KEY_OUT);
    }

    @Bean
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                                    AudioTranscriptionReceiver receiver) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(QUEUE_OUT_NAME);
        container.setMessageListener(receiver);
        return container;
    }
}
