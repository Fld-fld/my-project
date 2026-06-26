package com.example.express.service;

import com.example.express.dto.ExpressRequest;
import com.example.express.entity.Express;
import com.example.express.entity.Notification;
import com.example.express.entity.User;
import com.example.express.exception.BusinessException;
import com.example.express.mapper.ExpressMapper;
import com.example.express.mapper.NotificationMapper;
import com.example.express.mapper.UserMapper;
import com.example.express.util.PickupCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 快递服务
 */
@Service
public class ExpressService {
    
    @Autowired
    private ExpressMapper expressMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private NotificationMapper notificationMapper;
    
    /**
     * 根据手机号查询快递列表
     */
    public List<Express> getExpressListByPhone(String phone) {
        return expressMapper.findByRecipientPhone(phone);
    }
    
    /**
     * 根据手机号和状态查询快递
     */
    public List<Express> getExpressListByPhoneAndStatus(String phone, String status) {
        if (status == null || status.equals("ALL")) {
            return expressMapper.findByRecipientPhone(phone);
        }
        return expressMapper.findByRecipientPhoneAndStatus(phone, status);
    }
    
    /**
     * 根据ID查询快递
     */
    public Express getExpressById(Long id) {
        Express express = expressMapper.findById(id);
        if (express == null) {
            throw new BusinessException("快递不存在");
        }
        return express;
    }
    
    /**
     * 录入快递
     */
    @Transactional
    public Express addExpress(ExpressRequest request, Long adminId) {
        // 验证快递单号唯一
        if (expressMapper.findByTrackingNumber(request.getTrackingNumber()) != null) {
            throw new BusinessException("快递单号已存在");
        }
        
        // 生成取件码
        String pickupCode = generateUniquePickupCode();
        
        // 创建快递
        Express express = new Express();
        express.setTrackingNumber(request.getTrackingNumber());
        express.setCompany(request.getCompany());
        express.setRecipientName(request.getRecipientName());
        express.setRecipientPhone(request.getRecipientPhone());
        express.setLocation(request.getLocation());
        express.setPickupCode(pickupCode);
        express.setStatus("PENDING");
        express.setAdminId(adminId);
        
        expressMapper.insert(express);
        
        // 发送通知
        User user = userMapper.findByPhone(request.getRecipientPhone());
        if (user != null) {
            Notification notification = new Notification();
            notification.setUserId(user.getId());
            notification.setType("ARRIVAL");
            notification.setTitle("您的快递已到达");
            notification.setContent(String.format(
                "快递单号：%s\n快递公司：%s\n取件码：%s\n存放位置：%s",
                express.getTrackingNumber(),
                express.getCompany(),
                express.getPickupCode(),
                express.getLocation()
            ));
            notification.setIsRead(0);
            notificationMapper.insert(notification);
        }
        
        return express;
    }
    
    /**
     * 更新快递状态
     */
    @Transactional
    public void updateExpressStatus(Long id, String status) {
        Express express = expressMapper.findById(id);
        if (express == null) {
            throw new BusinessException("快递不存在");
        }
        
        String oldStatus = express.getStatus();
        expressMapper.updateStatus(id, status);
        
        if ("PICKED_UP".equals(status) && !"PICKED_UP".equals(oldStatus)) {
            User user = userMapper.findByPhone(express.getRecipientPhone());
            if (user != null) {
                Notification notification = new Notification();
                notification.setUserId(user.getId());
                notification.setType("PICKUP");
                notification.setTitle("快递已取件");
                notification.setContent(String.format(
                    "您的快递已成功取件\n快递单号：%s\n取件码：%s",
                    express.getTrackingNumber(),
                    express.getPickupCode()
                ));
                notification.setIsRead(0);
                notificationMapper.insert(notification);
            }
        }
    }
    
    /**
     * 补发快递到达通知
     */
    @Transactional
    public void resendArrivalNotification(Long expressId) {
        Express express = expressMapper.findById(expressId);
        if (express == null) {
            throw new BusinessException("快递不存在");
        }
        
        User user = userMapper.findByPhone(express.getRecipientPhone());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        Notification notification = new Notification();
        notification.setUserId(user.getId());
        notification.setType("ARRIVAL");
        notification.setTitle("您的快递已到达");
        notification.setContent(String.format(
            "快递单号：%s\n快递公司：%s\n取件码：%s\n存放位置：%s",
            express.getTrackingNumber(),
            express.getCompany(),
            express.getPickupCode(),
            express.getLocation()
        ));
        notification.setIsRead(0);
        notificationMapper.insert(notification);
    }
    
    /**
     * 获取所有快递列表
     */
    public List<Express> getAllExpressList() {
        return expressMapper.findAll();
    }
    
    /**
     * 根据状态获取快递列表
     */
    public List<Express> getExpressListByStatus(String status) {
        return expressMapper.findByStatus(status);
    }
    
    /**
     * 生成唯一取件码
     */
    private String generateUniquePickupCode() {
        String code;
        int count = 0;
        do {
            code = PickupCodeUtil.generateCode();
            count++;
            if (count > 100) {
                throw new BusinessException("取件码生成失败");
            }
        } while (expressMapper.findByPickupCode(code) != null);
        
        return code;
    }
}
