package com.example.express.service;

import com.example.express.dto.*;
import com.example.express.entity.User;
import com.example.express.exception.BusinessException;
import com.example.express.mapper.UserMapper;
import com.example.express.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * 用户认证服务
 */
@Service
public class AuthService {
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 用户注册
     */
    public UserVO register(RegisterRequest request) {
        // 验证手机号唯一
        if (userMapper.findByPhone(request.getPhone()) != null) {
            throw new BusinessException("手机号已被注册");
        }
        
        // 验证学号唯一
        if (userMapper.findByStudentId(request.getStudentId()) != null) {
            throw new BusinessException("学号已被注册");
        }
        
        // 创建用户
        User user = new User();
        user.setStudentId(request.getStudentId());
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setPassword(encryptPassword(request.getPassword()));
        user.setRole("STUDENT");
        
        userMapper.insert(user);
        
        return toUserVO(user);
    }
    
    /**
     * 用户登录
     */
    public LoginVO login(LoginRequest request) {
        User user = userMapper.findByPhone(request.getPhone());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        String inputPassword = request.getPassword();
        String encryptedInput = encryptPassword(inputPassword);
        
        System.out.println("=== 登录调试信息 ===");
        System.out.println("前端传入的密码：" + inputPassword);
        System.out.println("加密后的密码：" + encryptedInput);
        System.out.println("数据库中的密码：" + user.getPassword());
        System.out.println("明文匹配：" + user.getPassword().equals(inputPassword));
        System.out.println("密文匹配：" + user.getPassword().equals(encryptedInput));
        
        // 支持明文和密文两种输入方式
        boolean passwordMatch = user.getPassword().equals(encryptedInput) || 
                               user.getPassword().equals(inputPassword);
        
        if (!passwordMatch) {
            throw new BusinessException("密码错误");
        }
        
        // 生成Token
        String token = JwtUtil.generateToken(user.getId(), user.getPhone(), user.getRole());
        
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUser(toUserVO(user));
        
        return loginVO;
    }
    
    /**
     * 获取用户信息
     */
    public UserVO getUserInfo(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return toUserVO(user);
    }
    
    /**
     * 密码加密
     */
    private String encryptPassword(String password) {
        // 简单加密，实际应使用BCrypt
        return DigestUtils.md5DigestAsHex(("express" + password + "2024").getBytes());
    }
    
    /**
     * 发送验证码
     */
    public void sendVerificationCode(String phone) {
        User user = userMapper.findByPhone(phone);
        if (user == null) {
            throw new BusinessException("该手机号未注册");
        }
        
        String code = String.format("%06d", new java.util.Random().nextInt(999999));
        
        System.out.println("=== 用户验证码 ===");
        System.out.println("手机号: " + phone);
        System.out.println("验证码: " + code);
        
        verificationCodeStore.put(phone, code);
    }
    
    /**
     * 重置密码
     */
    public void resetPassword(String phone, String code, String newPassword) {
        String storedCode = verificationCodeStore.get(phone);
        if (storedCode == null || !storedCode.equals(code)) {
            throw new BusinessException("验证码错误");
        }
        
        User user = userMapper.findByPhone(phone);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        user.setPassword(encryptPassword(newPassword));
        userMapper.update(user);
        
        verificationCodeStore.remove(phone);
    }
    
    /**
     * 实体转VO
     */
    private UserVO toUserVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setStudentId(user.getStudentId());
        vo.setName(user.getName());
        vo.setPhone(user.getPhone());
        vo.setRole(user.getRole());
        return vo;
    }
    
    private java.util.Map<String, String> verificationCodeStore = new java.util.HashMap<>();
}
