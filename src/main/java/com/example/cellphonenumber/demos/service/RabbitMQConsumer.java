package com.example.cellphonenumber.demos.service;

import com.example.cellphonenumber.demos.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumer {
    
    @RabbitListener(queues = RabbitMQConfig.DIRECT_QUEUE_NAME)
    public void receiveDirectMessage(String message) {
        System.out.println("Received direct message: " + message);
        // 处理消息逻辑
    }
    
    @RabbitListener(queues = RabbitMQConfig.TOPIC_QUEUE_NAME)
    public void receiveTopicMessage(String message) {
        System.out.println("Received topic message: " + message);
        // 处理消息逻辑
    }
    
    @RabbitListener(queues = RabbitMQConfig.FANOUT_QUEUE_NAME)
    public void receiveFanoutMessage(String message) {
        System.out.println("Received fanout message: " + message);
        // 处理消息逻辑
    }
}