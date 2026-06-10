package com.example.express.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 预约请求
 */
@Data
public class ReservationRequest {
    private Long expressId;           // 快递ID
    private LocalDate reservationDate; // 预约日期
    private LocalTime startTime;      // 开始时间
    private LocalTime endTime;        // 结束时间
}
