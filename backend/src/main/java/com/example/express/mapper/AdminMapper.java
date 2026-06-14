package com.example.express.mapper;

import com.example.express.entity.Admin;
import org.apache.ibatis.annotations.*;

@Mapper
public interface AdminMapper {
    
    @Select("SELECT * FROM admin WHERE username = #{username}")
    Admin findByUsername(String username);
    
    @Select("SELECT * FROM admin WHERE id = #{id}")
    Admin findById(Long id);
    
    @Update("UPDATE admin SET password = #{password}, updated_at = NOW() WHERE id = #{id}")
    void update(Admin admin);
}
