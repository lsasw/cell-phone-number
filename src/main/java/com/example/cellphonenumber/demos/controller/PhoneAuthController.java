package com.example.cellphonenumber.demos.controller;

import com.example.cellphonenumber.demos.mapper.UserA;
import com.example.cellphonenumber.demos.service.SmsService;
import com.example.cellphonenumber.demos.service.UserService;
import com.example.cellphonenumber.demos.service.VerificationCodeService;
import com.example.cellphonenumber.demos.utils.JWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
public class PhoneAuthController {

    private static final Logger logger = LoggerFactory.getLogger(PhoneAuthController.class);

    @Autowired
    @Qualifier("smsServiceImpl")
    private SmsService smsService;

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    // 手机号正则表达式
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 发送验证码接口
     * @param phone 手机号
     * @return 响应结果
     */
    @PostMapping("/send-code")
    public ResponseEntity<Map<String, Object>> sendVerificationCode(@RequestParam String phone) {
        logger.info("Sending verification code request for phone: {}", phone);

        Map<String, Object> result = new HashMap<>();

        // 验证手机号格式
        if (!isValidPhone(phone)) {
            logger.warn("Invalid phone number format: {}", phone);
            result.put("success", false);
            result.put("message", "手机号格式不正确");
            return ResponseEntity.badRequest().body(result);
        }

        try {
            // 发送验证码
            String code = smsService.sendVerificationCode(phone);

            // 保存验证码
            verificationCodeService.saveCode(phone, code);

            logger.info("Verification code sent successfully to phone: {}", phone);
            result.put("success", true);
            result.put("message", "验证码已发送");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to send verification code to phone: {}", phone, e);
            result.put("success", false);
            result.put("message", "发送验证码失败: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 手机号登录/注册接口
     * @param phone 手机号
     * @param code 验证码
     * @return 响应结果，包含token等信息
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginOrRegister(@RequestParam String phone,
                                                               @RequestParam String code) {
        logger.info("Login/register request for phone: {}", phone);

        Map<String, Object> result = new HashMap<>();

        // 验证手机号格式
        if (!isValidPhone(phone)) {
            logger.warn("Invalid phone number format: {}", phone);
            result.put("success", false);
            result.put("message", "手机号格式不正确");
            return ResponseEntity.badRequest().body(result);
        }

        // 验证验证码
        if (!verificationCodeService.verifyCode(phone, code)) {
            logger.warn("Invalid verification code for phone: {}", phone);
            result.put("success", false);
            result.put("message", "验证码错误或已过期");
            return ResponseEntity.badRequest().body(result);
        }

        try {
            // 查找用户
            UserA userA = userService.findByPhone(phone);
            boolean isRegister = false;

            // 如果用户不存在则创建新用户
            if (userA == null) {
                userA = new UserA();
                userA.setPhone(phone);
                userA.setName("用户" + phone.substring(7)); // 简单设置用户名
                userA.setAge(18);
                userA = userService.save(userA);
                isRegister = true;
                logger.info("New user registered with phone: {}", phone);
                result.put("message", "注册成功");
            } else {
                logger.info("User logged in with phone: {}", phone);
                result.put("message", "登录成功");
            }

            // 生成JWT token
            String token = JWTUtil.generateToken(phone);

            result.put("success", true);
            result.put("token", token);
            result.put("user", userA);
            result.put("isRegister", isRegister);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to login/register user with phone: {}", phone, e);
            result.put("success", false);
            result.put("message", "登录/注册失败: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 验证手机号格式
     * @param phone 手机号
     * @return 是否有效
     */
    private boolean isValidPhone(String phone) {
        return StringUtils.isNotBlank(phone) && PHONE_PATTERN.matcher(phone).matches();
    }
}