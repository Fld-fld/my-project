package com.example.express.controller;

import com.example.express.entity.Notice;
import com.example.express.service.NoticeService;
import com.example.express.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 公告控制器
 */
@RestController
@RequestMapping("/admin/notice")
public class NoticeController {
    
    @Autowired
    private NoticeService noticeService;
    
    /**
     * 获取所有公告
     */
    @GetMapping("/list")
    public Result<List<Notice>> getNoticeList() {
        List<Notice> notices = noticeService.getAllNotices();
        return Result.success(notices);
    }
    
    /**
     * 获取公告详情
     */
    @GetMapping("/{id}")
    public Result<Notice> getNoticeById(@PathVariable Long id) {
        Notice notice = noticeService.getNoticeById(id);
        return Result.success(notice);
    }
    
    /**
     * 创建公告
     */
    @PostMapping
    public Result<Notice> createNotice(@RequestBody Map<String, Object> body) {
        String title = (String) body.get("title");
        String content = (String) body.get("content");
        Long adminId = body.get("adminId") != null ? ((Number) body.get("adminId")).longValue() : 1L;
        
        Notice notice = noticeService.createNotice(title, content, adminId);
        return Result.success(notice);
    }
    
    /**
     * 更新公告
     */
    @PutMapping("/{id}")
    public Result<Notice> updateNotice(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String title = (String) body.get("title");
        String content = (String) body.get("content");
        Integer status = body.get("status") != null ? ((Number) body.get("status")).intValue() : 1;
        
        Notice notice = noticeService.updateNotice(id, title, content, status);
        return Result.success(notice);
    }
    
    /**
     * 删除公告
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return Result.success("删除成功");
    }
}