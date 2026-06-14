package com.example.express.controller;

import com.example.express.dto.*;
import com.example.express.service.AdminAuthService;
import com.example.express.service.AdminService;
import com.example.express.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员认证控制器
 */
@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {
    
    @Autowired
    private AdminAuthService adminAuthService;
    
    @Autowired
    private AdminService adminService;
    
    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody AdminLoginRequest request) {
        LoginVO loginVO = adminAuthService.login(request);
        return Result.success("登录成功", loginVO);
    }
    
    /**
     * 发送验证码
     */
    @PostMapping("/send-code")
    public Result<String> sendCode(@RequestParam String username) {
        adminService.sendVerificationCode(username);
        return Result.success("验证码已发送");
    }
    
    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    public Result<String> resetPassword(@RequestBody AdminResetPasswordRequest request) {
        adminService.resetPassword(request.getUsername(), request.getCode(), request.getNewPassword());
        return Result.success("密码重置成功");
    }
}
