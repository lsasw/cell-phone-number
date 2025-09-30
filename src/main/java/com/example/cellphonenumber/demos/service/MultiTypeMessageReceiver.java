package com.example.cellphonenumber.demos.service;

import com.example.cellphonenumber.demos.mapper.User;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "multi.type.queue")
public class MultiTypeMessageReceiver {

    @RabbitHandler
    public void handleString(String message) {
        System.out.println("Received a string: " + message);
    }

    @RabbitHandler
    public void handleUser(User user) { // 假设配置了JSON消息转换器
        System.out.println("Received a user: " + user.getUsername());
    }

    // 可以添加更多的@RabbitHandler方法来处理其他类型
}