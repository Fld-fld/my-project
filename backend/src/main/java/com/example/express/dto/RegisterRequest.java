package com.example.express.dto;

import lombok.Data;

/**
 * 用户注册请求
 */
@Data
public class RegisterRequest {
    private String studentId;   // 学号
    private String name;       // 姓名
    private String phone;      // 手机号
    private String password;   // 密码
}
