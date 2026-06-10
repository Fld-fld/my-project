package com.example.express.dto;

import lombok.Data;

/**
 * 管理员登录请求
 */
@Data
public class AdminLoginRequest {
    private String username;    // 用户名
    private String password;    // 密码
}
