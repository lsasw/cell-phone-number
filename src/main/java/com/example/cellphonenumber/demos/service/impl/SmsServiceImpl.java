package com.example.cellphonenumber.demos.service.impl;

import com.example.cellphonenumber.demos.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class SmsServiceImpl implements SmsService {
    
    private static final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);
    
    // ANSI颜色代码
    private static final String RESET = "\u001B[0m";
    private static final String BLACK = "\u001B[30m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";
    
    @Override
    public String sendVerificationCode(String phone) {
        // 生成6位随机验证码
        String code = String.format("%06d", new Random().nextInt(1000000));
        
        // 在实际项目中，这里应该调用真正的短信服务发送验证码
        // 比如阿里云短信服务、腾讯云短信服务等
        String[] colors = {RED, GREEN, YELLOW, BLUE, PURPLE, CYAN};
        StringBuilder coloredCode = new StringBuilder();
        for (int i = 0; i < code.length(); i++) {
            char digit = code.charAt(i);
            coloredCode.append(colors[i % colors.length])
                       .append(digit)
                       .append(RESET);
        }
        
        logger.info("Sending verification code {} to phone number: {}", code, phone);
        System.out.println("验证码已发送至 " + phone + ": " + coloredCode);
        
        // 模拟发送短信的网络延迟
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return code;
    }
}