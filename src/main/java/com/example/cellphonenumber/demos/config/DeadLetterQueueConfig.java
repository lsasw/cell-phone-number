package com.example.cellphonenumber.demos.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DeadLetterQueueConfig {
    
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("dead_letter_exchange");
    }
    
    @Bean
    public Queue deadLetterQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "dead_letter_exchange");
        args.put("x-dead-letter-routing-key", "dead_letter_routing_key");
        return new Queue("dead_letter_queue", true, false, false, args);
    }
    
    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with("dead_letter_routing_key");
    }
}