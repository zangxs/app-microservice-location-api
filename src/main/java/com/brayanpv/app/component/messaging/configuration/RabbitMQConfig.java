package com.brayanpv.app.component.messaging.configuration;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.queue}")
    private String queue;

    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;

    @Value("${app.rabbitmq.status-queue}")
    private String statusQueue;

    @Value("${app.rabbitmq.status-routing-key}")
    private String statusRoutingKey;

    @Bean
    public DirectExchange landscapeExchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    public Queue landscapeQueue() {
        return new Queue(queue, true);
    }

    @Bean
    public Binding binding(Queue landscapeQueue, DirectExchange landscapeExchange) {
        return BindingBuilder
                .bind(landscapeQueue)
                .to(landscapeExchange)
                .with(routingKey);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    @Bean
    public Queue landscapeStatusQueue() {
        return new Queue(statusQueue, true);
    }

    @Bean
    public Binding statusBinding(Queue landscapeStatusQueue, DirectExchange landscapeExchange) {
        return BindingBuilder
                .bind(landscapeStatusQueue)
                .to(landscapeExchange)
                .with(statusRoutingKey);
    }
}
