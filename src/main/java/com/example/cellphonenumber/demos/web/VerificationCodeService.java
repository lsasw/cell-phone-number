package com.example.cellphonenumber.demos.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    // 验证码过期时间（分钟）
    private static final long CODE_EXPIRE_MINUTES = 5;
    
    /**
     * 保存验证码
     * @param phone 手机号
     * @param code 验证码
     */
    public void saveCode(String phone, String code) {
        redisTemplate.opsForValue().set("verification_code:" + phone, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
    }
    
    /**
     * 验证验证码
     * @param phone 手机号
     * @param code 验证码
     * @return 是否验证通过
     */
    public boolean verifyCode(String phone, String code) {
        if (StringUtils.isBlank(phone) || StringUtils.isBlank(code)) {
            return false;
        }
        
        String key = "verification_code:" + phone;
        Object storedCode = redisTemplate.opsForValue().get(key);
        
        if (storedCode == null) {
            return false;
        }
        
        boolean isMatch = code.equals(storedCode.toString());
        
        // 验证成功后删除验证码
        if (isMatch) {
            redisTemplate.delete(key);
        }
        
        return isMatch;
    }
}