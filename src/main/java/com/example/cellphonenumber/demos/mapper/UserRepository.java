package com.example.cellphonenumber.demos.mapper;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * 根据手机号查找用户
     * @param phone 手机号
     * @return 用户对象
     */
    Optional<User> findByPhone(String phone);
    
    /**
     * 检查手机号是否存在
     * @param phone 手机号
     * @return 是否存在
     */
    boolean existsByPhone(String phone);
}