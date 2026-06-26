package com.example.express.mapper;

import com.example.express.entity.Express;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ExpressMapper {
    
    @Select("SELECT * FROM express WHERE id = #{id}")
    Express findById(Long id);
    
    @Select("SELECT * FROM express WHERE tracking_number = #{trackingNumber}")
    Express findByTrackingNumber(String trackingNumber);
    
    @Select("SELECT * FROM express WHERE pickup_code = #{pickupCode}")
    Express findByPickupCode(String pickupCode);
    
    @Select("SELECT * FROM express WHERE recipient_phone = #{recipientPhone} ORDER BY created_at DESC")
    List<Express> findByRecipientPhone(String recipientPhone);
    
    @Select("SELECT * FROM express WHERE recipient_phone = #{recipientPhone} AND status = #{status} ORDER BY created_at DESC")
    List<Express> findByRecipientPhoneAndStatus(String recipientPhone, String status);
    
    @Select("SELECT * FROM express ORDER BY created_at DESC")
    List<Express> findAll();
    
    @Select("SELECT * FROM express WHERE status = #{status} ORDER BY created_at DESC")
    List<Express> findByStatus(String status);
    
    @Insert("INSERT INTO express (tracking_number, company, recipient_name, recipient_phone, " +
            "location, pickup_code, status, admin_id) " +
            "VALUES (#{trackingNumber}, #{company}, #{recipientName}, #{recipientPhone}, " +
            "#{location}, #{pickupCode}, #{status}, #{adminId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Express express);
    
    @Update("UPDATE express SET status=#{status} WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    @Update("UPDATE express SET tracking_number=#{trackingNumber}, company=#{company}, " +
            "recipient_name=#{recipientName}, recipient_phone=#{recipientPhone}, " +
            "location=#{location} WHERE id=#{id}")
    int update(Express express);
    
    @Delete("DELETE FROM express WHERE id = #{id}")
    int deleteById(Long id);
    
    @Select("SELECT COUNT(*) FROM express")
    Long countAll();
    
    @Select("SELECT COUNT(*) FROM express WHERE status = #{status}")
    Long countByStatus(String status);
    
    @Select("SELECT COUNT(*) FROM express WHERE DATE(created_at) = CURDATE()")
    Long countToday();
}
