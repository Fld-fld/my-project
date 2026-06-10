package com.example.express.dto;

import lombok.Data;

/**
 * 用户信息响应
 */
@Data
public class UserVO {
    private Long id;
    private String studentId;
    private String name;
    private String phone;
    private String role;
}
