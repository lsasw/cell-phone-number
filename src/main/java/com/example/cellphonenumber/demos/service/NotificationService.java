package com.example.cellphonenumber.demos.service;


import com.example.cellphonenumber.demos.config.RabbitMQConfig;
import com.example.cellphonenumber.demos.mapper.User;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    // 控制是否触发异常（用于测试 DLQ）
    private boolean simulateFailure = false;

    public void setSimulateFailure(boolean fail) {
        this.simulateFailure = fail;
    }

    @RabbitListener(queues = RabbitMQConfig.USER_NOTIFICATION_QUEUE)
    public void handleUserRegistration(User user) throws Exception {
        System.out.println("[NotificationService] 已收到以下注册事件: " + user.getEmail());

        if (simulateFailure) {
            throw new RuntimeException("模拟通知服务异常！");
        }

        sendEmail(user);
        sendSMS(user);
    }

    private void sendEmail(User user) {
        System.out.println("[EMAIL已发送] 欢迎, " + user.getUsername() + ", 您的帐户已创建。");
    }

    private void sendSMS(User user) {
        System.out.println("[短信已发送] 你好 " + user.getUsername() + ", 欢迎来到我们的平台!");
    }
}