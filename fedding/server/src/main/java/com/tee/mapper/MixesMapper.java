package com.tee.mapper;

import com.tee.entity.Mixes;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 加料信息数据访问层接口
 * 提供加料信息的增删改查操作
 */
@Mapper
public interface MixesMapper {

    /**
     * 插入加料记录
     * @param mixes 加料信息对象
     * @return 影响行数
     */
    @Insert("INSERT INTO mixes_info (tank_id, apply_user_id, shift_type, material_name, product_spec, plan_weight, " +
            "bottom_weight, full_weight, flame_retardant_weight, actual_weight, apply_time, feeding_time, " +
            "feeding_user_id, return_time, return_user_id, status, remark, create_time, update_time) " +
            "VALUES (#{tankId}, #{applyUserId}, #{shiftType}, #{materialName}, #{productSpec}, #{planWeight}, " +
            "#{bottomWeight}, #{fullWeight}, #{flameRetardantWeight}, #{actualWeight}, #{applyTime}, #{feedingTime}, " +
            "#{feedingUserId}, #{returnTime}, #{returnUserId}, #{status}, #{remark}, datetime('now'), datetime('now'))")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Mixes mixes);

    /**
     * 根据ID更新加料记录
     * @param mixes 加料信息对象
     * @return 影响行数
     */
    @Update("UPDATE mixes_info SET tank_id = #{tankId}, apply_user_id = #{applyUserId}, " +
            "shift_type = #{shiftType}, material_name = #{materialName}, product_spec = #{productSpec}, " +
            "plan_weight = #{planWeight}, bottom_weight = #{bottomWeight}, full_weight = #{fullWeight}, " +
            "flame_retardant_weight = #{flameRetardantWeight}, actual_weight = #{actualWeight}, " +
            "apply_time = #{applyTime}, feeding_time = #{feedingTime}, feeding_user_id = #{feedingUserId}, " +
            "return_time = #{returnTime}, return_user_id = #{returnUserId}, status = #{status}, " +
            "remark = #{remark}, update_time = datetime('now') WHERE id = #{id}")
    int updateById(Mixes mixes);

    /**
     * 根据ID删除加料记录
     * @param id 加料记录ID
     * @return 影响行数
     */
    @Delete("DELETE FROM mixes_info WHERE id = #{id}")
    int deleteById(Integer id);

    /**
     * 根据ID查询加料记录
     * @param id 加料记录ID
     * @return 加料信息对象
     */
    @Select("SELECT f.*, t.tank_no, " +
            "u1.user_name as apply_user_name, u1.account as apply_user_account, " +
            "u2.user_name as feeding_user_name, u2.account as feeding_user_account, " +
            "u3.user_name as return_user_name, u3.account as return_user_account " +
            "FROM mixes_info f " +
            "LEFT JOIN tank_info t ON f.tank_id = t.id " +
            "LEFT JOIN user_info u1 ON f.apply_user_id = u1.id " +
            "LEFT JOIN user_info u2 ON f.feeding_user_id = u2.id " +
            "LEFT JOIN user_info u3 ON f.return_user_id = u3.id " +
            "WHERE f.id = #{id}")
    Mixes selectById(Integer id);

    /**
     * 查询所有加料记录
     * @return 加料记录列表
     */
    @Select("SELECT f.*, t.tank_no, " +
            "u1.user_name as apply_user_name, u1.account as apply_user_account, " +
            "u2.user_name as feeding_user_name, u2.account as feeding_user_account, " +
            "u3.user_name as return_user_name, u3.account as return_user_account " +
            "FROM mixes_info f " +
            "LEFT JOIN tank_info t ON f.tank_id = t.id " +
            "LEFT JOIN user_info u1 ON f.apply_user_id = u1.id " +
            "LEFT JOIN user_info u2 ON f.feeding_user_id = u2.id " +
            "LEFT JOIN user_info u3 ON f.return_user_id = u3.id " +
            "ORDER BY f.create_time DESC")
    List<Mixes> selectAll();

    /**
     * 根据料罐编号查询加料记录
     * @param tankNo 料罐编号
     * @return 加料记录列表
     */
    @Select("SELECT f.*, t.tank_no, " +
            "u1.user_name as apply_user_name, u1.account as apply_user_account, " +
            "u2.user_name as feeding_user_name, u2.account as feeding_user_account, " +
            "u3.user_name as return_user_name, u3.account as return_user_account " +
            "FROM mixes_info f " +
            "LEFT JOIN tank_info t ON f.tank_id = t.id " +
            "LEFT JOIN user_info u1 ON f.apply_user_id = u1.id " +
            "LEFT JOIN user_info u2 ON f.feeding_user_id = u2.id " +
            "LEFT JOIN user_info u3 ON f.return_user_id = u3.id " +
            "WHERE t.tank_no = #{tankNo} " +
            "ORDER BY f.create_time DESC")
    List<Mixes> selectByTankNo(String tankNo);

    /**
     * 根据申请用户ID查询加料记录
     * @param applyUserId 申请用户ID
     * @return 加料记录列表
     */
    @Select("SELECT f.*, t.tank_no, " +
            "u1.user_name as apply_user_name, u1.account as apply_user_account, " +
            "u2.user_name as feeding_user_name, u2.account as feeding_user_account, " +
            "u3.user_name as return_user_name, u3.account as return_user_account " +
            "FROM mixes_info f " +
            "LEFT JOIN tank_info t ON f.tank_id = t.id " +
            "LEFT JOIN user_info u1 ON f.apply_user_id = u1.id " +
            "LEFT JOIN user_info u2 ON f.feeding_user_id = u2.id " +
            "LEFT JOIN user_info u3 ON f.return_user_id = u3.id " +
            "WHERE f.apply_user_id = #{applyUserId} " +
            "ORDER BY f.create_time DESC")
    List<Mixes> selectByApplyUserId(Integer applyUserId);
} 