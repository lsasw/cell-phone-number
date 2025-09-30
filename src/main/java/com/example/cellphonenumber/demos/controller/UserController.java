package com.example.cellphonenumber.demos.controller;


import com.example.cellphonenumber.demos.mapper.User;
import com.example.cellphonenumber.demos.service.NotificationService;
import com.example.cellphonenumber.demos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        userService.registerUser(user);
        return "用户注册成功!";
    }

    @PostMapping("/trigger-failure")
    public String triggerFailure(@RequestParam boolean fail) {
        notificationService.setSimulateFailure(fail);
        return "模拟故障模式设置为: " + fail;
    }
}