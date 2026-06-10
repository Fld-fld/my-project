package com.example.express.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 快递实体类
 */
@Data
public class Express {
    private Long id;
    private String trackingNumber;  // 快递单号
    private String company;          // 快递公司
    private String recipientName;    // 收件人姓名
    private String recipientPhone;   // 收件人手机号
    private String location;         // 存放位置
    private String pickupCode;       // 取件码
    private String status;           // 状态
    private Long adminId;            // 录入管理员ID
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
