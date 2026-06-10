package com.example.express.mapper;

import com.example.express.entity.Admin;
import org.apache.ibatis.annotations.*;

@Mapper
public interface AdminMapper {
    
    @Select("SELECT * FROM admin WHERE username = #{username}")
    Admin findByUsername(String username);
    
    @Select("SELECT * FROM admin WHERE id = #{id}")
    Admin findById(Long id);
}
