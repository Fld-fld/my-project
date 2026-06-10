package com.example.express.mapper;

import com.example.express.entity.Notification;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NotificationMapper {
    
    @Select("SELECT * FROM notification WHERE id = #{id}")
    Notification findById(Long id);
    
    @Select("SELECT * FROM notification WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Notification> findByUserId(Long userId);
    
    @Select("SELECT * FROM notification WHERE user_id = #{userId} AND is_read = #{isRead} ORDER BY created_at DESC")
    List<Notification> findByUserIdAndIsRead(@Param("userId") Long userId, @Param("isRead") Integer isRead);
    
    @Select("SELECT COUNT(*) FROM notification WHERE user_id = #{userId} AND is_read = 0")
    Integer countUnread(Long userId);
    
    @Insert("INSERT INTO notification (user_id, type, title, content, is_read) " +
            "VALUES (#{userId}, #{type}, #{title}, #{content}, #{isRead})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Notification notification);
    
    @Update("UPDATE notification SET is_read = 1 WHERE id = #{id}")
    int markAsRead(Long id);
    
    @Update("UPDATE notification SET is_read = 1 WHERE user_id = #{userId}")
    int markAllAsRead(Long userId);
    
    @Delete("DELETE FROM notification WHERE id = #{id}")
    int deleteById(Long id);
}
