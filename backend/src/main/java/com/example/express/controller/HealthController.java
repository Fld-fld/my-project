package com.example.express.controller;

import com.example.express.util.Result;
import org.springframework.web.bind.annotation.*;

/**
 * 健康检查
 */
@RestController
@RequestMapping("/api")
public class HealthController {
    
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("Server is running");
    }
}
