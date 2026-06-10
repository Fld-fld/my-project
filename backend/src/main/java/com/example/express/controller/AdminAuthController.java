package com.example.express.controller;

import com.example.express.dto.*;
import com.example.express.service.AdminAuthService;
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
    
    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody AdminLoginRequest request) {
        LoginVO loginVO = adminAuthService.login(request);
        return Result.success("登录成功", loginVO);
    }
}
