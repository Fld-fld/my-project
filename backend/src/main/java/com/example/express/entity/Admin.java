package com.example.express.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 管理员实体类
 */
@Data
public class Admin {
    private Long id;
    private String username;     // 用户名
    private String password;     // 密码
    private String role;         // 角色
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
