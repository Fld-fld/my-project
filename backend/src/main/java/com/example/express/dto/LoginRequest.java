package com.example.express.dto;

import lombok.Data;

/**
 * 用户登录请求
 */
@Data
public class LoginRequest {
    private String phone;      // 手机号
    private String password;   // 密码
}
