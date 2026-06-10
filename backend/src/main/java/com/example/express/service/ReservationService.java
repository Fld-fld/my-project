package com.example.express.service;

import com.example.express.dto.ReservationRequest;
import com.example.express.entity.Express;
import com.example.express.entity.Reservation;
import com.example.express.exception.BusinessException;
import com.example.express.mapper.ExpressMapper;
import com.example.express.mapper.ReservationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约服务
 */
@Service
public class ReservationService {
    
    private static final int MAX_CAPACITY_PER_SLOT = 20;  // 每时段最大预约人数
    private static final int CANCEL_HOURS_LIMIT = 1;    // 取消预约提前小时数
    
    @Autowired
    private ReservationMapper reservationMapper;
    
    @Autowired
    private ExpressMapper expressMapper;
    
    /**
     * 创建预约
     */
    @Transactional
    public Reservation createReservation(ReservationRequest request, Long userId) {
        // 验证快递是否存在
        Express express = expressMapper.findById(request.getExpressId());
        if (express == null) {
            throw new BusinessException("快递不存在");
        }
        
        // 验证快递状态
        if (!"PENDING".equals(express.getStatus()) && !"RESERVED".equals(express.getStatus())) {
            throw new BusinessException("该快递不可预约");
        }
        
        // 验证预约日期
        if (request.getReservationDate().isBefore(LocalDate.now())) {
            throw new BusinessException("预约日期不能早于今天");
        }
        
        // 验证时段冲突
        int count = reservationMapper.countByDateAndTime(request.getReservationDate(), request.getStartTime());
        if (count >= MAX_CAPACITY_PER_SLOT) {
            throw new BusinessException("该时段已约满，请选择其他时段");
        }
        
        // 验证用户当天预约数量
        int userCount = reservationMapper.countByUserAndDate(userId, request.getReservationDate());
        if (userCount > 0) {
            throw new BusinessException("您当天已有预约");
        }
        
        // 创建预约
        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setExpressId(request.getExpressId());
        reservation.setReservationDate(request.getReservationDate());
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setStatus("PENDING");
        
        reservationMapper.insert(reservation);
        
        // 更新快递状态
        expressMapper.updateStatus(request.getExpressId(), "RESERVED");
        
        return reservation;
    }
    
    /**
     * 取消预约
     */
    @Transactional
    public void cancelReservation(Long reservationId, Long userId) {
        Reservation reservation = reservationMapper.findById(reservationId);
        if (reservation == null) {
            throw new BusinessException("预约不存在");
        }
        
        if (!reservation.getUserId().equals(userId)) {
            throw new BusinessException("无权取消此预约");
        }
        
        if ("CANCELLED".equals(reservation.getStatus())) {
            throw new BusinessException("预约已取消");
        }
        
        // 验证取消时间
        LocalDateTime reservationTime = reservation.getReservationDate()
                .atTime(reservation.getStartTime());
        LocalDateTime now = LocalDateTime.now();
        long hoursUntil = java.time.Duration.between(now, reservationTime).toHours();
        
        if (hoursUntil < CANCEL_HOURS_LIMIT) {
            throw new BusinessException("预约时间前1小时不可取消");
        }
        
        // 更新状态
        reservationMapper.updateStatus(reservationId, "CANCELLED");
        
        // 更新快递状态
        expressMapper.updateStatus(reservation.getExpressId(), "PENDING");
    }
    
    /**
     * 查询用户预约列表
     */
    public List<Reservation> getUserReservations(Long userId) {
        return reservationMapper.findByUserId(userId);
    }
    
    /**
     * 查询所有预约列表
     */
    public List<Reservation> getAllReservations() {
        return reservationMapper.findAll();
    }
    
    /**
     * 根据日期查询预约
     */
    public List<Reservation> getReservationsByDate(LocalDate date) {
        return reservationMapper.findByDate(date);
    }
    
    /**
     * 完成预约
     */
    @Transactional
    public void completeReservation(Long reservationId) {
        reservationMapper.updateStatus(reservationId, "COMPLETED");
        Reservation reservation = reservationMapper.findById(reservationId);
        if (reservation != null) {
            expressMapper.updateStatus(reservation.getExpressId(), "PICKED_UP");
        }
    }
}
