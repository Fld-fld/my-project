package com.example.express.service;

import com.example.express.dto.StatisticsVO;
import com.example.express.mapper.ExpressMapper;
import com.example.express.mapper.ReservationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * 统计服务
 */
@Service
public class StatisticsService {
    
    @Autowired
    private ExpressMapper expressMapper;
    
    @Autowired
    private ReservationMapper reservationMapper;
    
    /**
     * 获取统计数据
     */
    public StatisticsVO getStatistics() {
        StatisticsVO vo = new StatisticsVO();
        
        vo.setTotalExpress(expressMapper.countAll());
        vo.setPendingExpress(expressMapper.countByStatus("PENDING"));
        vo.setPickedUpExpress(expressMapper.countByStatus("PICKED_UP"));
        vo.setTodayArrivals(expressMapper.countToday());
        vo.setTodayReservations(reservationMapper.countByDate(LocalDate.now()));
        
        // 计算取件率
        if (vo.getTotalExpress() > 0) {
            vo.setPickupRate(Math.round(vo.getPickedUpExpress() * 100.0 / vo.getTotalExpress() * 100) / 100.0);
        } else {
            vo.setPickupRate(0.0);
        }
        
        return vo;
    }
}
