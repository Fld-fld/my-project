package com.example.express.service;

import com.example.express.entity.Notification;
import com.example.express.exception.BusinessException;
import com.example.express.mapper.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通知服务
 */
@Service
public class NotificationService {
    
    @Autowired
    private NotificationMapper notificationMapper;
    
    /**
     * 获取用户通知列表
     */
    public List<Notification> getUserNotifications(Long userId) {
        return notificationMapper.findByUserId(userId);
    }
    
    /**
     * 获取未读通知
     */
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationMapper.findByUserIdAndIsRead(userId, 0);
    }
    
    /**
     * 获取未读数量
     */
    public Integer getUnreadCount(Long userId) {
        return notificationMapper.countUnread(userId);
    }
    
    /**
     * 标记通知已读
     */
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationMapper.findById(notificationId);
        if (notification == null) {
            throw new BusinessException("通知不存在");
        }
        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此通知");
        }
        notificationMapper.markAsRead(notificationId);
    }
    
    /**
     * 标记所有通知已读
     */
    public void markAllAsRead(Long userId) {
        notificationMapper.markAllAsRead(userId);
    }
}
