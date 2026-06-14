package com.example.express.controller;

import com.example.express.entity.User;
import com.example.express.service.UserService;
import com.example.express.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/admin/user")
public class UserManageController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取所有用户列表
     */
    @GetMapping("/list")
    public Result<List<User>> getUserList() {
        List<User> users = userService.getAllUsers();
        return Result.success(users);
    }
    
    /**
     * 根据ID获取用户详情
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return Result.success(user);
    }
    
    /**
     * 更新用户状态（禁用/启用）
     */
    @PutMapping("/{id}/status")
    public Result<String> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateUserStatus(id, status);
        return Result.success("操作成功");
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success("删除成功");
    }
}