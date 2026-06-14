package com.example.express.mapper;

import com.example.express.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    
    @Select("SELECT * FROM user WHERE phone = #{phone}")
    User findByPhone(String phone);
    
    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);
    
    @Select("SELECT * FROM user WHERE student_id = #{studentId}")
    User findByStudentId(String studentId);
    
    @Insert("INSERT INTO user (student_id, name, phone, password, role) " +
            "VALUES (#{studentId}, #{name}, #{phone}, #{password}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    @Update("UPDATE user SET name=#{name}, phone=#{phone} WHERE id=#{id}")
    int update(User user);
    
    @Select("SELECT * FROM user")
    java.util.List<User> findAll();
    
    @Update("UPDATE user SET status=#{status} WHERE id=#{id}")
    int updateStatus(Long id, Integer status);
    
    @Delete("DELETE FROM user WHERE id=#{id}")
    int delete(Long id);
}
