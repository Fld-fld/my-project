package com.example.express.dto;

import lombok.Data;

/**
 * 统计数据响应
 */
@Data
public class StatisticsVO {
    private Long totalExpress;        // 总快递数
    private Long pendingExpress;     // 待取件数
    private Long pickedUpExpress;     // 已取件数
    private Double pickupRate;        // 取件率
    private Long todayArrivals;       // 今日到件
    private Long todayReservations;  // 今日预约
}
