package com.example.express.mapper;

import com.example.express.entity.Reservation;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Mapper
public interface ReservationMapper {
    
    @Select("SELECT * FROM reservation WHERE id = #{id}")
    Reservation findById(Long id);
    
    @Select("SELECT * FROM reservation WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Reservation> findByUserId(Long userId);
    
    @Select("SELECT * FROM reservation WHERE express_id = #{expressId} ORDER BY created_at DESC")
    List<Reservation> findByExpressId(Long expressId);
    
    @Select("SELECT * FROM reservation WHERE reservation_date = #{date} ORDER BY start_time")
    List<Reservation> findByDate(LocalDate date);
    
    @Select("SELECT * FROM reservation ORDER BY created_at DESC")
    List<Reservation> findAll();
    
    @Insert("INSERT INTO reservation (user_id, express_id, reservation_date, start_time, end_time, status) " +
            "VALUES (#{userId}, #{expressId}, #{reservationDate}, #{startTime}, #{endTime}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Reservation reservation);
    
    @Update("UPDATE reservation SET reservation_date=#{reservationDate}, start_time=#{startTime}, " +
            "end_time=#{endTime}, status=#{status} WHERE id=#{id}")
    int update(Reservation reservation);
    
    @Update("UPDATE reservation SET status=#{status} WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    @Delete("DELETE FROM reservation WHERE id = #{id}")
    int deleteById(Long id);
    
    // 查询某时段已预约人数
    @Select("SELECT COUNT(*) FROM reservation " +
            "WHERE reservation_date = #{date} AND start_time = #{startTime} AND status != 'CANCELLED'")
    int countByDateAndTime(@Param("date") LocalDate date, @Param("startTime") LocalTime startTime);
    
    // 查询用户某天是否有预约
    @Select("SELECT COUNT(*) FROM reservation " +
            "WHERE user_id = #{userId} AND reservation_date = #{date} AND status != 'CANCELLED'")
    int countByUserAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);
    
    @Select("SELECT COUNT(*) FROM reservation WHERE reservation_date = #{date}")
    Long countByDate(LocalDate date);
}
