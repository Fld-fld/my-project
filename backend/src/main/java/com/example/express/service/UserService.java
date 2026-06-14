package com.example.express.service;

import com.example.express.entity.User;
import com.example.express.exception.BusinessException;
import com.example.express.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户管理服务
 */
@Service
public class UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 获取所有用户列表
     */
    public List<User> getAllUsers() {
        return userMapper.findAll();
    }
    
    /**
     * 根据ID获取用户
     */
    public User getUserById(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }
    
    /**
     * 更新用户状态
     */
    public void updateUserStatus(Long id, Integer status) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        userMapper.updateStatus(id, status);
    }
    
    /**
     * 删除用户
     */
    public void deleteUser(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        userMapper.delete(id);
    }
}