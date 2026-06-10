package com.example.express.dto;

import lombok.Data;

/**
 * 快递录入请求
 */
@Data
public class ExpressRequest {
    private String trackingNumber;  // 快递单号
    private String company;         // 快递公司
    private String recipientName;   // 收件人姓名
    private String recipientPhone;  // 收件人手机号
    private String location;        // 存放位置
}
