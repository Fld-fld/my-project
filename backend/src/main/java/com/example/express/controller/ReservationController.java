package com.example.express.controller;

import com.example.express.dto.ReservationRequest;
import com.example.express.entity.Reservation;
import com.example.express.exception.BusinessException;
import com.example.express.service.ReservationService;
import com.example.express.util.JwtUtil;
import com.example.express.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 预约控制器
 */
@RestController
@RequestMapping("/api/reservation")
public class ReservationController {
    
    @Autowired
    private ReservationService reservationService;
    
    /**
     * 创建预约
     */
    @PostMapping
    public Result<Reservation> createReservation(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody ReservationRequest request) {
        if (token == null || !token.startsWith("Bearer ") || !JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            throw new BusinessException("请先登录");
        }
        Long userId = JwtUtil.getUserId(token.replace("Bearer ", ""));
        Reservation reservation = reservationService.createReservation(request, userId);
        return Result.success("预约成功", reservation);
    }
    
    /**
     * 取消预约
     */
    @DeleteMapping("/{id}")
    public Result<String> cancelReservation(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long id) {
        if (token == null || !token.startsWith("Bearer ") || !JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            throw new BusinessException("请先登录");
        }
        Long userId = JwtUtil.getUserId(token.replace("Bearer ", ""));
        reservationService.cancelReservation(id, userId);
        return Result.success("取消成功");
    }
    
    /**
     * 查询我的预约
     */
    @GetMapping("/my")
    public Result<List<Reservation>> getMyReservations(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ") || !JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            throw new BusinessException("请先登录");
        }
        Long userId = JwtUtil.getUserId(token.replace("Bearer ", ""));
        List<Reservation> list = reservationService.getUserReservations(userId);
        return Result.success(list);
    }
    
    /**
     * 管理员：查询所有预约
     */
    @GetMapping("/all")
    public Result<List<Reservation>> getAllReservations(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<Reservation> list;
        if (date != null) {
            list = reservationService.getReservationsByDate(date);
        } else {
            list = reservationService.getAllReservations();
        }
        return Result.success(list);
    }
    
    /**
     * 管理员：完成预约（确认取件）
     */
    @PutMapping("/{id}/complete")
    public Result<String> completeReservation(@PathVariable Long id) {
        reservationService.completeReservation(id);
        return Result.success("取件完成");
    }
}
