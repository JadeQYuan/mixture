package cn.domain.mapper;

import cn.domain.entity.Check;
import cn.domain.pojo.CheckQo;
import cn.domain.pojo.CheckVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 校验数据访问层接口
 */
@Mapper
public interface CheckMapper {

    @Select({
            "<script>",
            "SELECT f.id, f.tank_id as tankId, f.tank_no as tankNo, f.return_id as returnId, f.return_weight as returnWeight, ",
                    "f.bottom_id as bottomId, f.bottom_weight as bottomWeight, f.opinion, f.status, f.admin_opinion as adminOpinion, " +
                    "f.correct_weight as correctWeight, ",
            "u1.user_name as userName, u1.account as userAccount, ",
            "u2.user_name as adminName, u2.account as adminAccount ",
            "FROM check_info f ",
            "LEFT JOIN user_info u1 ON f.user_id = u1.id ",
            "LEFT JOIN user_info u2 ON f.admin_id = u2.id " +
            "<where>",
            "<if test='tankNo != null and tankNo != \"\"'>",
            "   AND f.tank_no LIKE '%' || #{tankNo} || '%'",
            "</if>",
            "<if test='status != null'>",
            "   AND f.status = #{status} ",
            "</if>" +
            "</where>",
            "</script>"
    })
    List<CheckVo> selectByCondition(CheckQo checkQo);

    @Select("SELECT f.id, f.tank_id as tankId, f.tank_no as tankNo, f.return_id as returnId, f.return_weight as returnWeight, " +
            "f.bottom_id as bottomId, f.bottom_weight as bottomWeight " +
            "FROM check_info f " +
            "WHERE f.id = #{id}")
    Check selectById(Integer id);

    @Insert("INSERT INTO check_info (tank_id, tank_no, return_id, return_weight, bottom_id, bottom_weight, opinion, user_id, " +
            "status, create_time, update_time) " +
            "VALUES (#{tankId}, #{tankNo}, #{returnId}, #{returnWeight}, #{bottomId}, #{bottomWeight}, #{opinion}, #{userId}, " +
            "0, datetime('now', 'localtime'), datetime('now', 'localtime'))")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void check(Check check);

    @Update("<script>" +
            "UPDATE check_info SET status = #{status}, " +
            "admin_opinion = #{adminOpinion}, " +
            "admin_id = #{adminId}, " +
            "<if test='status == 1'>" +
            "   correct_weight = #{correctWeight}, " +
            "</if>" +
            "update_time = datetime('now', 'localtime') " +
            "WHERE id = #{id}" +
            "</script>")
    void process(Check check);
}