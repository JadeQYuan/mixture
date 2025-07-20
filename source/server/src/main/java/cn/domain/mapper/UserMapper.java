package cn.domain.mapper;

import cn.domain.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户信息数据访问层接口
 */
@Mapper
public interface UserMapper {

    @Select("SELECT id, user_name as userName, account, role_code as roleCode, face_path as facePath " +
            "FROM user_info WHERE id = #{id} and del_flag = 1")
    User getUserInfo(Integer id);

    @Select({
        "<script>",
        "SELECT id, user_name as userName, account, role_code as roleCode, ",
        "face_path as facePath, face_feature as faceFeature, remark, create_time as createTime, update_time as updateTime ",
        "FROM user_info ",
        "<where>",
        "<if test='userName != null and userName != \"\"'>",
        "user_name LIKE '%' || #{userName} || '%'",
        "</if>",
        "<if test='account != null and account != \"\"'>",
        "AND account LIKE '%' || #{account} || '%'",
        "</if>",
        "and del_flag = 1",
        "</where>",
        "ORDER BY create_time DESC",
        "</script>"
    })
    List<User> getUserInfoByName(@Param("userName") String userName, @Param("account") String account);

    @Select("SELECT count(0) " +
            "FROM user_info WHERE account = #{account} and del_flag = 1")
    int checkUserByAccount(@Param("account") String account);

    @Select("SELECT id, user_name as userName, account, role_code as roleCode " +
            "FROM user_info WHERE password = #{password} AND account = #{account} and del_flag = 1")
    User getUserInfoByAccount(@Param("password") String password, @Param("account") String account);

    @Insert("INSERT INTO user_info (user_name, account, role_code, remark, del_flag, create_time, update_time) " +
            "VALUES (#{userName}, #{account},  #{roleCode}, #{remark}, 1, datetime('now', 'localtime'), datetime('now', 'localtime'))")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertUserInfo(User user);

    @Update({
        "<script>",
        "UPDATE user_info",
        "<set>",
        "<if test='userName != null'>user_name = #{userName},</if>",
        "<if test='account != null'>account = #{account},</if>",
        "<if test='roleCode != null'>role_code = #{roleCode},</if>",
        "<if test='remark != null'>remark = #{remark},</if>",
        "update_time = datetime('now', 'localtime')",
        "</set>",
        "WHERE id = #{id}",
        "</script>"
    })
    void updateUserInfo(User user);

    @Update("UPDATE user_info SET password = #{password}, update_time = datetime('now', 'localtime') WHERE id = #{id}")
    void updatePassword(User user);

    @Update("UPDATE user_info SET face_path = #{facePath}, face_feature = #{faceFeature}, update_time = datetime('now', 'localtime') WHERE id = #{id}")
    void updateFaceInfo(Integer id, String facePath, String faceFeature);

    @Update("UPDATE user_info SET del_flag = 0, update_time = datetime('now', 'localtime') WHERE id = #{id}")
    void deleteUserInfo(@Param("id") Integer id);
}
