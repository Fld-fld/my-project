package com.example.express.controller;

import com.example.express.dto.*;
import com.example.express.exception.BusinessException;
import com.example.express.service.AuthService;
import com.example.express.util.JwtUtil;
import com.example.express.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户认证控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody RegisterRequest request) {
        UserVO user = authService.register(request);
        return Result.success("注册成功", user);
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginRequest request) {
        LoginVO loginVO = authService.login(request);
        return Result.success("登录成功", loginVO);
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/userinfo")
    public Result<UserVO> getUserInfo(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ") || !JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            throw new BusinessException("请先登录");
        }
        Long userId = JwtUtil.getUserId(token.replace("Bearer ", ""));
        UserVO user = authService.getUserInfo(userId);
        return Result.success(user);
    }
    
    /**
     * 发送验证码
     */
    @PostMapping("/send-code")
    public Result<String> sendCode(@RequestParam String phone) {
        authService.sendVerificationCode(phone);
        return Result.success("验证码已发送");
    }
    
    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    public Result<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getPhone(), request.getCode(), request.getNewPassword());
        return Result.success("密码重置成功");
    }
}
