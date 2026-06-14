package com.example.express.service;

import com.example.express.entity.Admin;
import com.example.express.exception.BusinessException;
import com.example.express.mapper.AdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class AdminService {
    
    @Autowired
    private AdminMapper adminMapper;
    
    public void changePassword(Long adminId, String oldPassword, String newPassword) {
        Admin admin = adminMapper.findById(adminId);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }
        
        String encryptedOldPassword = encryptPassword(oldPassword);
        if (!admin.getPassword().equals(encryptedOldPassword)) {
            throw new BusinessException("旧密码错误");
        }
        
        admin.setPassword(encryptPassword(newPassword));
        adminMapper.update(admin);
    }
    
    public void sendVerificationCode(String username) {
        Admin admin = adminMapper.findByUsername(username);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }
        
        String code = String.format("%06d", new java.util.Random().nextInt(999999));
        
        System.out.println("=== 管理员验证码 ===");
        System.out.println("用户名: " + username);
        System.out.println("验证码: " + code);
        
        verificationCodeStore.put(username, code);
    }
    
    public void resetPassword(String username, String code, String newPassword) {
        String storedCode = verificationCodeStore.get(username);
        if (storedCode == null || !storedCode.equals(code)) {
            throw new BusinessException("验证码错误");
        }
        
        Admin admin = adminMapper.findByUsername(username);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }
        
        admin.setPassword(encryptPassword(newPassword));
        adminMapper.update(admin);
        
        verificationCodeStore.remove(username);
    }
    
    private String encryptPassword(String password) {
        return DigestUtils.md5DigestAsHex(("express" + password + "2024").getBytes());
    }
    
    private java.util.Map<String, String> verificationCodeStore = new java.util.HashMap<>();
}