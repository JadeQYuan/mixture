package com.tee.mapper;

import com.tee.entity.Tank;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 料罐信息数据访问层接口
 * 提供料罐信息的增删改查操作
 */
@Mapper
public interface TankMapper {

    @Select({
            "<script>",
            "SELECT t.id, t.tank_no as tankNo, t.remark, t.update_time as updateTime, u.user_name as userName, u.account as userAccount " +
            "FROM tank_info t ",
            "LEFT JOIN user_info u ON t.user_id = u.id " +
            "<where>",
            "<if test='tankNo != null and tankNo != \"\"'>",
            " t.tank_no LIKE '%' || #{tankNo} || '%'",
            "</if>",
            "</where>",
            "ORDER BY t.create_time DESC",
            "</script>"
    })
    List<Tank> selectByCondition(@Param("tankNo") String tankNo);

    /**
     * 根据ID查询料罐信息
     * @param id 料罐ID
     * @return 料罐信息对象
     */
    @Select("SELECT id, tank_no as tankNo, remark  " +
            "FROM tank_info " +
            "WHERE id = #{id}")
    Tank selectById(Integer id);

    /**
     * 根据料罐编号查询料罐信息
     * @param tankNo 料罐编号
     * @return 料罐信息列表
     */
    @Select("SELECT t.* " +
            "FROM tank_info t " +
            "WHERE t.tank_no = #{tankNo}")
    List<Tank> selectByTankNo(String tankNo);

    /**
     * 根据用户ID查询料罐信息
     * @param userId 用户ID
     * @return 料罐信息列表
     */
    @Select("SELECT t.*, u.user_name as current_user, u.account as current_account " +
            "FROM tank_info t " +
            "LEFT JOIN user_info u ON t.user_id = u.id " +
            "WHERE t.user_id = #{userId}")
    List<Tank> selectByUserId(String userId);

    /**
     * 查询可用料罐（未被占用的料罐）
     * @return 可用料罐列表
     */
    @Select("<script>" +
            "SELECT t.id, t.tank_no as tankNo, t.remark " +
            "FROM tank_info t " +
            "WHERE user_id is null" +
            "</script>")
    List<Tank> selectAvailableTanks();

    @Select("<script>" +
            "SELECT t.id, t.tank_no as tankNo, t.remark " +
            "FROM tank_info t " +
            "WHERE t.user_id = #{userId} " +
            "AND user_id is null " +
            "</script>")
    List<Tank> selectMyTanks(Integer userId);

    /**
     * 插入料罐信息
     * @param tank 料罐信息对象
     * @return 影响行数
     */
    @Insert("INSERT INTO tank_info (tank_no, remark, user_id, create_time, update_time) " +
            "VALUES (#{tankNo}, #{remark}, #{userId}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Tank tank);

    /**
     * 根据ID更新料罐信息
     * @param tank 料罐信息对象
     * @return 影响行数
     */
    @Update("UPDATE tank_info SET tank_no = #{tankNo}, remark = #{remark}, update_time = #{updateTime} WHERE id = #{id}")
    int updateById(Tank tank);

    @Update("UPDATE tank_info SET user_id = #{userId}, update_time = #{updateTime} WHERE id = #{id}")
    int updateUser(int id, int userId);

    /**
     * 根据ID删除料罐信息
     * @param id 料罐ID
     * @return 影响行数
     */
    @Delete("DELETE FROM tank_info WHERE id = #{id}")
    int deleteById(Integer id);
}