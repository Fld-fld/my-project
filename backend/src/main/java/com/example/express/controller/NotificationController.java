package com.example.express.controller;

import com.example.express.entity.Notification;
import com.example.express.exception.BusinessException;
import com.example.express.service.NotificationService;
import com.example.express.util.JwtUtil;
import com.example.express.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 通知控制器
 */
@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * 获取通知列表
     */
    @GetMapping("/list")
    public Result<List<Notification>> getNotifications(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ") || !JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            throw new BusinessException("请先登录");
        }
        Long userId = JwtUtil.getUserId(token.replace("Bearer ", ""));
        List<Notification> list = notificationService.getUserNotifications(userId);
        return Result.success(list);
    }
    
    /**
     * 获取未读通知
     */
    @GetMapping("/unread")
    public Result<List<Notification>> getUnreadNotifications(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ") || !JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            throw new BusinessException("请先登录");
        }
        Long userId = JwtUtil.getUserId(token.replace("Bearer ", ""));
        List<Notification> list = notificationService.getUnreadNotifications(userId);
        return Result.success(list);
    }
    
    /**
     * 获取未读数量
     */
    @GetMapping("/unread/count")
    public Result<Map<String, Integer>> getUnreadCount(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ") || !JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            throw new BusinessException("请先登录");
        }
        Long userId = JwtUtil.getUserId(token.replace("Bearer ", ""));
        Integer count = notificationService.getUnreadCount(userId);
        return Result.success(Map.of("count", count));
    }
    
    /**
     * 标记通知已读
     */
    @PutMapping("/{id}/read")
    public Result<String> markAsRead(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long id) {
        if (token == null || !token.startsWith("Bearer ") || !JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            throw new BusinessException("请先登录");
        }
        Long userId = JwtUtil.getUserId(token.replace("Bearer ", ""));
        notificationService.markAsRead(id, userId);
        return Result.success("标记成功");
    }
    
    /**
     * 标记所有通知已读
     */
    @PutMapping("/read/all")
    public Result<String> markAllAsRead(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ") || !JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            throw new BusinessException("请先登录");
        }
        Long userId = JwtUtil.getUserId(token.replace("Bearer ", ""));
        notificationService.markAllAsRead(userId);
        return Result.success("标记成功");
    }
}
