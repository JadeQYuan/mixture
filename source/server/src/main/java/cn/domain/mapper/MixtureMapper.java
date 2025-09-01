package cn.domain.mapper;

import cn.domain.entity.Mixture;
import cn.domain.pojo.MixtureBottomVo;
import cn.domain.pojo.MixtureQo;
import cn.domain.pojo.MixtureVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 混合料信息数据访问层接口
 */
@Mapper
public interface MixtureMapper {

    @Select({
            "<script>",
            "SELECT f.id, f.tank_id as tankId, f.tank_no as tankNo, f.shift_type as shiftType, f.status, " +
                    "f.material_name as materialName, f.product_spec as productSpec, f.plan_weight as planWeight, " +
                    "f.bottom_weight as bottomWeight, f.full_weight as fullWeight, f.flame_retardant_weight as flameRetardantWeight, " +
                    "f.return_weight as returnWeight, f.actual_weight as actualWeight, " +
                    "f.apply_time as applyTime, f.picking_time as pickingTime, f.feeding_time as feedingTime, f.return_time as returnTime, f.remark, ",
            "u1.user_name as applyUserName, u1.account as applyUserAccount, ",
            "u2.user_name as feedingUserName, u2.account as feedingUserAccount, ",
            "u3.user_name as pickingUserName, u3.account as pickingUserAccount ",
            "FROM mixture_info f ",
            "LEFT JOIN tank_info t ON f.tank_id = t.id ",
            "LEFT JOIN user_info u1 ON f.apply_user_id = u1.id ",
            "LEFT JOIN user_info u2 ON f.feeding_user_id = u2.id ",
            "LEFT JOIN user_info u3 ON f.picking_user_id = u3.id ",
            "<where>",
            "<if test='applyUserKey != null and applyUserKey != \"\"'>",
            "AND (u1.user_name LIKE '%' || #{applyUserKey} || '%' or u1.account LIKE '%' || #{applyUserKey} || '%')",
            "</if>",
            "<if test='pickingUserKey != null and pickingUserKey != \"\"'>",
            "AND (u3.user_name LIKE '%' || #{pickingUserKey} || '%' or u3.account LIKE '%' || #{pickingUserKey} || '%')",
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
            "<if test='status != null and status.size > 0'>",
            "AND f.status IN ",
            "<foreach collection='status' item='item' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</if>",
            "<if test='applyStartTime != null and applyEndTime != null'>",
            "AND f.apply_time &gt;= #{applyStartTime} AND f.apply_time &lt;= #{applyEndTime}",
            "</if>",
            "<if test='pickingStartTime != null and pickingEndTime != null'>",
            "AND f.picking_time &gt;= #{pickingStartTime} AND f.picking_time &lt;= #{pickingEndTime}",
            "</if>",
            "<if test='feedingStartTime != null and feedingEndTime != null'>",
            "AND f.feeding_time &gt;= #{feedingStartTime} AND f.feeding_time &lt;= #{feedingEndTime}",
            "</if>",
            "<if test='returnStartTime != null and returnEndTime != null'>",
            "AND f.return_time &gt;= #{returnStartTime} AND f.return_time &lt;= #{returnEndTime}",
            "</if>",
            "</where>",
            "</script>"
    })
    List<MixtureVo> selectByCondition(MixtureQo mixtureQo);

    /**
     * 插入加料记录
     * @param mixture 加料信息对象
     * @return 影响行数
     */
    @Insert("INSERT INTO mixture_info (tank_id, tank_no, apply_user_id, picking_user_id, shift_type, material_name, product_spec, plan_weight, " +
            "apply_time, picking_time, status, create_time, update_time) " +
            "VALUES (#{tankId}, #{tankNo}, #{applyUserId}, #{pickingUserId}, #{shiftType}, #{materialName}, #{productSpec}, #{planWeight}, " +
            "datetime('now', 'localtime'), datetime('now', 'localtime'), 0, datetime('now', 'localtime'), datetime('now', 'localtime'))")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int apply(Mixture mixture);

    @Insert("INSERT INTO mixture_info (tank_id, tank_no, apply_user_id, shift_type, material_name, product_spec, plan_weight, " +
            "apply_time, status, create_time, update_time) " +
            "VALUES (#{tankId}, #{tankNo}, #{applyUserId}, #{shiftType}, #{materialName}, #{productSpec}, #{planWeight}, " +
            "datetime('now', 'localtime'), 3, datetime('now', 'localtime'), datetime('now', 'localtime'))")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void prepare(Mixture mixture);

    /**
     * 根据ID查询加料记录
     * @param id 加料记录ID
     * @return 加料信息对象
     */
    @Select("SELECT f.id, f.tank_id as tankId, f.tank_no as tankNo, f.shift_type as shiftType, f.status, " +
            "f.material_name as materialName, f.product_spec as productSpec, f.plan_weight as planWeight, " +
            "f.bottom_weight as bottomWeight, f.full_weight as fullWeight, f.flame_retardant_weight as flameRetardantWeight, " +
            "f.return_weight as returnWeight, " +
            "f.apply_time as applyTime, f.feeding_time as feedingTime, f.return_time as returnTime, f.remark " +
            "FROM mixture_info f " +
            "WHERE f.id = #{id}")
    Mixture selectById(Integer id);

    /**
     * 查询料罐上次退料的重量
     * @param tankId
     * @return
     */
    @Select("SELECT id, return_weight as returnWeight " +
            "FROM mixture_info " +
            "WHERE tank_id = #{tankId} and status = 2 " +
            "ORDER BY create_time DESC " +
            "LIMIT 1 ")
    Mixture getReturnWeight(Integer tankId);

    @Update("UPDATE mixture_info SET bottom_weight = #{bottomWeight} WHERE id = #{id}")
    void bottom(MixtureBottomVo bottomVo);

    @Update("UPDATE mixture_info SET full_weight = #{fullWeight}, " +
            "flame_retardant_weight = #{flameRetardantWeight}, " +
            "feeding_time = datetime('now', 'localtime'), feeding_user_id = #{feedingUserId}, " +
            "status = #{status} WHERE id = #{id}")
    void executeFeed(Mixture mixture);

    @Select("<script>" +
            "SELECT m.id, m.tank_id as tankId, m.tank_no as tankNo, m.apply_time as applyTime, m.status, m.shift_type as shiftType, m.status, " +
            "m.material_name as materialName, m.product_spec as productSpec, m.plan_weight as planWeight, " +
            "m.bottom_weight as bottomWeight, m.full_weight as fullWeight, m.flame_retardant_weight as flameRetardantWeight " +
            "FROM mixture_info m " +
            "WHERE m.status = 4 " +
            "</script>")
    List<Mixture> getTankForPicking();

    @Update("UPDATE mixture_info SET picking_user_id = #{pickingUserId}, " +
            "picking_time = datetime('now', 'localtime'), update_time = datetime('now', 'localtime'), status = 1 " +
            "WHERE id = #{id}")
    void executePicking(Mixture mixture);

    @Select("<script>" +
            "SELECT m.id, m.tank_id as tankId, m.apply_time as applyTime, m.status, t.tank_no as tankNo, t.remark " +
            "FROM mixture_info m " +
            "LEFT JOIN tank_info t ON m.tank_id = t.id " +
            "WHERE m.status in (0, 1, 3, 4) " +
            "AND t.user_id = #{userId} " +
            "</script>")
    List<Mixture> getTankForReturn(Integer userId);

    @Update("UPDATE mixture_info SET return_weight = #{returnWeight}, actual_weight = #{actualWeight}, " +
            "return_time = datetime('now', 'localtime'), update_time = datetime('now', 'localtime'), status = 2 " +
            "WHERE id = #{id}")
    void executeReturn(Mixture mixture);

    @Update("UPDATE mixture_info SET return_weight = #{returnWeight}, actual_weight = #{actualWeight}, " +
            "return_time = datetime('now', 'localtime'), update_time = datetime('now', 'localtime') " +
            "WHERE id = #{id}")
    void updateReturn(Integer id, Double returnWeight, Double actualWeight);

    @Update("UPDATE mixture_info SET update_time = datetime('now', 'localtime'), status = -1 " +
            "WHERE id = #{id}")
    void executeCancel(Mixture mixture);

    @Update("UPDATE mixture_info SET remark = #{remark}, material_name = #{materialName}, product_spec = #{productSpec}, " +
            "update_time = datetime('now', 'localtime') " +
            "WHERE id = #{id}")
    void updateRemark(Mixture mixture);
}