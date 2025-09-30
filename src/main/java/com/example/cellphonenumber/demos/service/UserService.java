package com.example.cellphonenumber.demos.service;

import com.example.cellphonenumber.demos.config.RabbitMQConfig;
import com.example.cellphonenumber.demos.mapper.User;
import com.example.cellphonenumber.demos.mapper.UserA;
import com.example.cellphonenumber.demos.mapper.UserRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void registerUser(User user) {
        System.out.println("[UserService] 正在注册用户: " + user.getUsername());

        // 模拟保存数据库...

        // 异步发送注册完成事件
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.USER_REGISTRATION_EXCHANGE,
                RabbitMQConfig.USER_ROUTING_KEY,
                user
        );
    }
    /**
     * 根据手机号查找用户
     * @param phone 手机号
     * @return 用户对象
     */
    public UserA findByPhone(String phone) {
        Optional<UserA> userOptional = userRepository.findByPhone(phone);
        return userOptional.orElse(null);
    }
    
    /**
     * 保存用户
     * @param userA 用户对象
     * @return 保存后的用户对象
     */
    public UserA save(UserA userA) {
        return userRepository.save(userA);
    }
    
    /**
     * 根据手机号检查用户是否存在
     * @param phone 手机号
     * @return 是否存在
     */
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }
}