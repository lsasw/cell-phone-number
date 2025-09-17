package com.example.cellphonenumber.demos.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 根据手机号查找用户
     * @param phone 手机号
     * @return 用户对象
     */
    public User findByPhone(String phone) {
        Optional<User> userOptional = userRepository.findByPhone(phone);
        return userOptional.orElse(null);
    }
    
    /**
     * 保存用户
     * @param user 用户对象
     * @return 保存后的用户对象
     */
    public User save(User user) {
        return userRepository.save(user);
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