package cn.domain.controller;

import cn.domain.entity.User;
import cn.domain.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    private UserService userService;

    /**
     * 账号密码登录
     */
    @PostMapping("/account")
    public String accountLogin(@RequestBody User user) {
        return userService.loginByAccount(user);
    }

    /**
     * 人脸登录认证
     */
    @PostMapping("/face")
    public String faceLogin(@RequestParam(value = "imageFile") MultipartFile multipartFile) throws Exception{
        return userService.loginByFace(multipartFile);
    }
} 