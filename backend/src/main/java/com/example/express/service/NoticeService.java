package com.example.express.service;

import com.example.express.entity.Notice;
import com.example.express.exception.BusinessException;
import com.example.express.mapper.NoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 公告服务
 */
@Service
public class NoticeService {
    
    @Autowired
    private NoticeMapper noticeMapper;
    
    /**
     * 获取所有公告
     */
    public List<Notice> getAllNotices() {
        return noticeMapper.findAll();
    }
    
    /**
     * 获取发布中的公告
     */
    public List<Notice> getActiveNotices() {
        return noticeMapper.findActive();
    }
    
    /**
     * 根据ID获取公告
     */
    public Notice getNoticeById(Long id) {
        Notice notice = noticeMapper.findById(id);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }
        return notice;
    }
    
    /**
     * 创建公告
     */
    public Notice createNotice(String title, String content, Long adminId) {
        Notice notice = new Notice();
        notice.setTitle(title);
        notice.setContent(content);
        notice.setStatus(1);
        notice.setAdminId(adminId);
        notice.setCreateTime(new Date());
        notice.setUpdateTime(new Date());
        
        noticeMapper.insert(notice);
        return notice;
    }
    
    /**
     * 更新公告
     */
    public Notice updateNotice(Long id, String title, String content, Integer status) {
        Notice notice = noticeMapper.findById(id);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }
        
        notice.setTitle(title);
        notice.setContent(content);
        notice.setStatus(status);
        notice.setUpdateTime(new Date());
        
        noticeMapper.update(notice);
        return notice;
    }
    
    /**
     * 删除公告
     */
    public void deleteNotice(Long id) {
        Notice notice = noticeMapper.findById(id);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }
        noticeMapper.delete(id);
    }
}