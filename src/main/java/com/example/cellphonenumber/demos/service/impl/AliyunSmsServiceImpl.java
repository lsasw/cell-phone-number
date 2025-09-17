package com.example.cellphonenumber.demos.service.impl;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.aliyun.teaopenapi.models.Config;
import com.example.cellphonenumber.demos.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Random;

@Service
public class AliyunSmsServiceImpl implements SmsService {

    private static final Logger logger = LoggerFactory.getLogger(AliyunSmsServiceImpl.class);

    private Client client;

    @Value("${aliyun.sms.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.sms.accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.sms.signName}")
    private String signName;

    @Value("${aliyun.sms.templateCode}")
    private String templateCode;

    @PostConstruct
    public void init() {
        try {
            Config config = new Config()
                    .setAccessKeyId(accessKeyId)
                    .setAccessKeySecret(accessKeySecret)
                    .setEndpoint("dysmsapi.aliyuncs.com");
            client = new Client(config);
        } catch (Exception e) {
            logger.error("Failed to initialize Aliyun SMS client", e);
        }
    }

    @Override
    public String sendVerificationCode(String phone) {
        // 生成6位随机验证码
        String code = String.format("%06d", new Random().nextInt(1000000));

        try {
            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                    .setPhoneNumbers(phone)
                    .setSignName(signName)
                    .setTemplateCode(templateCode)
                    .setTemplateParam("{\"code\":\"" + code + "\"}");

            SendSmsResponse response = client.sendSms(sendSmsRequest);
            SendSmsResponseBody body = response.getBody();

            if (!"OK".equals(body.getCode())) {
                logger.error("Failed to send SMS, code: {}, message: {}", body.getCode(), body.getMessage());
                throw new RuntimeException("Failed to send SMS: " + body.getMessage());
            }

            logger.info("Verification code sent successfully to phone: {}", phone);
            return code;
        } catch (Exception e) {
            logger.error("Failed to send verification code to phone: {}", phone, e);
            throw new RuntimeException("Failed to send verification code", e);
        }
    }
}