package com.example.express.service;

import com.example.express.dto.AdminLoginRequest;
import com.example.express.dto.LoginVO;
import com.example.express.dto.UserVO;
import com.example.express.entity.Admin;
import com.example.express.exception.BusinessException;
import com.example.express.mapper.AdminMapper;
import com.example.express.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * 管理员认证服务
 */
@Service
public class AdminAuthService {
    
    @Autowired
    private AdminMapper adminMapper;
    
    /**
     * 管理员登录
     */
    public LoginVO login(AdminLoginRequest request) {
        Admin admin = adminMapper.findByUsername(request.getUsername());
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }
        
        String inputPassword = request.getPassword();
        String encryptedInput = encryptPassword(inputPassword);
        
        // 支持明文和密文两种输入方式
        boolean passwordMatch = admin.getPassword().equals(encryptedInput) || 
                               admin.getPassword().equals(inputPassword) ||
                               admin.getPassword().equals(DigestUtils.md5DigestAsHex(inputPassword.getBytes()));
        
        if (!passwordMatch) {
            throw new BusinessException("密码错误");
        }
        
        // 生成Token
        String token = JwtUtil.generateToken(admin.getId(), admin.getUsername(), admin.getRole());
        
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        
        UserVO userVO = new UserVO();
        userVO.setId(admin.getId());
        userVO.setName(admin.getUsername());
        userVO.setRole(admin.getRole());
        loginVO.setUser(userVO);
        
        return loginVO;
    }
    
    /**
     * 密码加密
     */
    private String encryptPassword(String password) {
        return DigestUtils.md5DigestAsHex(("express" + password + "2024").getBytes());
    }
}
