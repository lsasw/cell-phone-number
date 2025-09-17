package com.example.cellphonenumber.demos.service;

import org.springframework.stereotype.Service;

@Service
public interface
SmsService {
    /**
     * 发送短信验证码
     * 在实际项目中，这里应该调用真正的短信服务发送验证码
     * 比如阿里云短信服务、腾讯云短信服务等
     * 
     * @param phone 手机号
     * @return 验证码
     */
    String sendVerificationCode(String phone);
}