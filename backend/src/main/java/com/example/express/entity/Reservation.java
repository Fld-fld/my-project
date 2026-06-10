package com.example.express.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 预约实体类
 */
@Data
public class Reservation {
    private Long id;
    private Long userId;              // 用户ID
    private Long expressId;           // 快递ID
    private LocalDate reservationDate; // 预约日期
    private LocalTime startTime;       // 开始时间
    private LocalTime endTime;         // 结束时间
    private String status;             // 状态
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
