package com.example.express.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
public class User {
    private Long id;
    private String studentId;    // 学号
    private String name;         // 姓名
    private String phone;       // 手机号
    private String password;    // 密码
    private String role;        // 角色
    private Integer status;     // 状态 0-禁用 1-启用
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}