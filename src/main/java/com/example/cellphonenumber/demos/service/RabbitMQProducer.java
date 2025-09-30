package com.example.cellphonenumber.demos.service;

import com.example.cellphonenumber.demos.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQProducer {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    /**
     * 发送Direct类型消息
     */
    public void sendDirectMessage(String message) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.DIRECT_EXCHANGE_NAME,
            RabbitMQConfig.DIRECT_ROUTING_KEY,
            message
        );
    }
    
    /**
     * 发送Topic类型消息
     */
    public void sendTopicMessage(String message) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.TOPIC_EXCHANGE_NAME,
            RabbitMQConfig.TOPIC_ROUTING_KEY,
            message
        );
    }
    
    /**
     * 发送Fanout类型消息
     */
    public void sendFanoutMessage(String message) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.FANOUT_EXCHANGE_NAME,
            "", // Fanout交换机不需要路由键
            message
        );
    }
}