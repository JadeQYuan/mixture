package com.tee.mapper;

import com.tee.entity.Mixes;
import com.tee.entity.Tank;
import com.tee.pojo.MixesQo;
import com.tee.pojo.MixesVo;
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
    @Insert("INSERT INTO mixes_info (tank_id, tank_no, apply_user_id, shift_type, material_name, product_spec, plan_weight, " +
            "apply_time, status, create_time, update_time) " +
            "VALUES (#{tankId}, #{tankNo}, #{applyUserId}, #{shiftType}, #{materialName}, #{productSpec}, #{planWeight}, " +
            "datetime('now'), 0, datetime('now'), datetime('now'))")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Mixes mixes);


    /**
     * 根据ID查询加料记录
     * @param id 加料记录ID
     * @return 加料信息对象
     */
    @Select("SELECT f.id, f.tank_id as tankId, f.tank_no as tankNo, f.shift_type as shiftType," +
            "f.material_name as materialName, f.product_spec as productSpec, f.plan_weight as planWeight, " +
            "f.bottom_weight as bottomWeight, f.full_weight as fullWeight, f.flame_retardant_weight as flameRetardantWeight, " +
            "f.return_weight as returnWeight, " +
            "f.apply_time as applyTime, f.feeding_time as feedingTime, f.return_time as returnTime, f.remark " +
            "FROM mixes_info f " +
            "WHERE f.id = #{id}")
    Mixes selectById(Integer id);

    @Select({
        "<script>",
        "SELECT f.id, f.tank_id as tankId, f.tank_no as tankNo, f.shift_type as shiftType," +
        "f.material_name as materialName, f.product_spec as productSpec, f.plan_weight as planWeight, " +
        "f.bottom_weight as bottomWeight, f.full_weight as fullWeight, f.flame_retardant_weight as flameRetardantWeight, " +
        "f.return_weight as returnWeight, f.actual_weight as actualWeight, " +
        "f.apply_time as applyTime, f.feeding_time as feedingTime, f.return_time as returnTime, f.remark, ",
        "u1.user_name as applyUserName, u1.account as applyUserAccount, ",
        "u2.user_name as feedingUserName, u2.account as feedingUserAccount ",
        "FROM mixes_info f ",
        "LEFT JOIN tank_info t ON f.tank_id = t.id ",
        "LEFT JOIN user_info u1 ON f.apply_user_id = u1.id ",
        "LEFT JOIN user_info u2 ON f.feeding_user_id = u2.id ",
        "<where>",
        "<if test='applyUserKey != null and applyUserKey != \"\"'>",
        "AND (u1.user_name LIKE '%' || #{applyUserKey} || '%' or u1.account LIKE '%' || #{applyUserKey} || '%')",
        "</if>",
        "<if test='tankNo != null and tankNo != \"\"'>",
        "AND t.tank_no LIKE '%' || #{tankNo} || '%'",
        "</if>",
        "<if test='shiftType != null and shiftType != \"\"'>",
        "AND f.shift_type = #{shiftType}",
        "</if>",
        "<if test='materialName != null and materialName != \"\"'>",
        "AND f.material_name = #{materialName} ",
        "</if>",
        "<if test='status != null '>",
        "AND f.status = #{status} ",
        "</if>",
        "</where>",
        "ORDER BY f.create_time DESC",
        "</script>"
    })
    List<MixesVo> selectByCondition(MixesQo mixesQo);

    @Update("UPDATE mixes_info SET bottom_weight = #{bottomWeight}, full_weight = #{fullWeight}, " +
            "flame_retardant_weight = #{flameRetardantWeight}, " +
            "feeding_time = datetime('now'), feeding_user_id = #{feedingUserId}, " +
            "status = 1 WHERE id = #{id}")
    void executeMixes(Mixes mixes);

    @Update("UPDATE mixes_info SET return_weight = #{returnWeight}, actual_weight = #{actualWeight}, " +
            "return_time = datetime('now'), status = 2 " +
            "WHERE id = #{id}")
    void executeReturn(Mixes mixes);

    @Update("UPDATE mixes_info SET remark = #{remark}, " +
            "update_time = datetime('now'), status = 2 " +
            "WHERE id = #{id}")
    void updateRemark(Mixes mixes);

    @Select("<script>" +
            "SELECT m.id, m.tank_id as tankId, t.tank_no as tankNo, t.remark " +
            "FROM mixes_info m " +
            "LEFT JOIN tank_info t ON m.tank_id = t.id " +
            "WHERE m.status = 1 " +
            "AND t.user_id = #{userId} " +
            "</script>")
    List<Mixes> getTankForReturn(Integer userId);
}