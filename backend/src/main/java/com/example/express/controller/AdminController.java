package com.example.express.controller;

import com.example.express.dto.StatisticsVO;
import com.example.express.service.StatisticsService;
import com.example.express.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 统计控制器
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private StatisticsService statisticsService;
    
    /**
     * 获取统计数据
     */
    @GetMapping("/statistics")
    public Result<StatisticsVO> getStatistics() {
        StatisticsVO vo = statisticsService.getStatistics();
        return Result.success(vo);
    }
}
