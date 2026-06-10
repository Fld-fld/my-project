package com.example.express.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 通知实体类
 */
@Data
public class Notification {
    private Long id;
    private Long userId;      // 用户ID
    private String type;      // 通知类型
    private String title;     // 标题
    private String content;   // 内容
    private Integer isRead;   // 是否已读
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
