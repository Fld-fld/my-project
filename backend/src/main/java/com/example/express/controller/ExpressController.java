package com.example.express.controller;

import com.example.express.dto.ExpressRequest;
import com.example.express.entity.Express;
import com.example.express.exception.BusinessException;
import com.example.express.service.ExpressService;
import com.example.express.util.JwtUtil;
import com.example.express.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 快递控制器
 */
@RestController
@RequestMapping("/api/express")
public class ExpressController {
    
    @Autowired
    private ExpressService expressService;
    
    /**
     * 用户查询自己的快递列表
     */
    @GetMapping("/list")
    public Result<List<Express>> getExpressList(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(required = false) String status) {
        // 验证token
        if (token == null || !token.startsWith("Bearer ") || !JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            throw new BusinessException("请先登录");
        }
        
        String phone = JwtUtil.parseToken(token.replace("Bearer ", "")).getSubject();
        List<Express> list = expressService.getExpressListByPhoneAndStatus(phone, status);
        return Result.success(list);
    }
    
    /**
     * 获取快递详情
     */
    @GetMapping("/{id}")
    public Result<Express> getExpressDetail(@PathVariable Long id) {
        Express express = expressService.getExpressById(id);
        return Result.success(express);
    }
    
    /**
     * 管理员：获取所有快递列表
     */
    @GetMapping("/all")
    public Result<List<Express>> getAllExpressList(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(required = false) String status) {
        if (token == null || !token.startsWith("Bearer ") || !JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            throw new BusinessException("请先登录");
        }
        String role = JwtUtil.getRole(token.replace("Bearer ", ""));
        if (!"ADMIN".equals(role)) {
            throw new BusinessException("无权限操作");
        }
        List<Express> list;
        if (status != null && !status.equals("ALL")) {
            list = expressService.getExpressListByStatus(status);
        } else {
            list = expressService.getAllExpressList();
        }
        return Result.success(list);
    }
    
    /**
     * 管理员：录入快递
     */
    @PostMapping
    public Result<Express> addExpress(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody ExpressRequest request) {
        if (token == null || !token.startsWith("Bearer ") || !JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            throw new BusinessException("请先登录");
        }
        String role = JwtUtil.getRole(token.replace("Bearer ", ""));
        if (!"ADMIN".equals(role)) {
            throw new BusinessException("无权限操作");
        }
        Long adminId = JwtUtil.getUserId(token.replace("Bearer ", ""));
        Express express = expressService.addExpress(request, adminId);
        return Result.success("录入成功", express);
    }
    
    /**
     * 更新快递状态
     */
    @PutMapping("/{id}/status")
    public Result<String> updateExpressStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        expressService.updateExpressStatus(id, status);
        return Result.success("更新成功");
    }
    
    /**
     * 补发快递到达通知
     */
    @PostMapping("/{id}/resend-notification")
    public Result<String> resendNotification(@PathVariable Long id) {
        expressService.resendArrivalNotification(id);
        return Result.success("通知已补发");
    }
}
