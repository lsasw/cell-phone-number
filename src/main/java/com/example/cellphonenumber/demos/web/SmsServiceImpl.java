package com.example.cellphonenumber.demos.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
//@Primary
public class SmsServiceImpl implements SmsService {
    
    private static final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);
    
    @Override
    public String sendVerificationCode(String phone) {
        // 生成6位随机验证码
        String code = String.format("%06d", new Random().nextInt(1000000));
        
        // 在实际项目中，这里应该调用真正的短信服务发送验证码
        // 比如阿里云短信服务、腾讯云短信服务等
        logger.info("Sending verification code {} to phone number: {}", code, phone);
        
        // 模拟发送短信的网络延迟
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return code;
    }
}