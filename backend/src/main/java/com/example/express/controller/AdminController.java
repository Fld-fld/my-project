package com.example.express.controller;

import com.example.express.dto.StatisticsVO;
import com.example.express.service.AdminService;
import com.example.express.service.StatisticsService;
import com.example.express.util.JwtUtil;
import com.example.express.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private StatisticsService statisticsService;
    
    @Autowired
    private AdminService adminService;
    
    /**
     * 获取统计数据
     */
    @GetMapping("/statistics")
    public Result<StatisticsVO> getStatistics() {
        StatisticsVO vo = statisticsService.getStatistics();
        return Result.success(vo);
    }
    
    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<String> changePassword(@RequestParam String oldPassword, 
                                        @RequestParam String newPassword,
                                        @RequestHeader("Authorization") String token) {
        String adminToken = token.replace("Bearer ", "");
        Long adminId = JwtUtil.getUserId(adminToken);
        adminService.changePassword(adminId, oldPassword, newPassword);
        return Result.success("密码修改成功");
    }
}
