package com.example.express.mapper;

import com.example.express.entity.Notice;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 公告Mapper接口
 */
@Mapper
public interface NoticeMapper {
    
    @Select("SELECT * FROM notice ORDER BY create_time DESC")
    List<Notice> findAll();
    
    @Select("SELECT * FROM notice WHERE id = #{id}")
    Notice findById(Long id);
    
    @Insert("INSERT INTO notice(title, content, status, admin_id, create_time, update_time) " +
            "VALUES(#{title}, #{content}, #{status}, #{adminId}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Notice notice);
    
    @Update("UPDATE notice SET title=#{title}, content=#{content}, status=#{status}, update_time=#{updateTime} WHERE id=#{id}")
    void update(Notice notice);
    
    @Delete("DELETE FROM notice WHERE id=#{id}")
    void delete(Long id);
    
    @Select("SELECT * FROM notice WHERE status = 1 ORDER BY create_time DESC")
    List<Notice> findActive();
}