package com.tee.mapper;

import com.tee.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT id, user_name as userName, account, password, role_code as roleCode, " +
            "face_path as facePath, face_feature as faceFeature, remark, create_time as createTime, update_time as updateTime " +
            "FROM user_info WHERE id = #{id}")
    User getUserInfo(@Param("id") Integer id);

    @Select("SELECT id, user_name as userName, account, password, role_code as roleCode, " +
            "face_path as facePath, face_feature as faceFeature, remark, create_time as createTime, update_time as updateTime " +
            "FROM user_info WHERE user_name LIKE '%' || #{userName} || '%' ORDER BY create_time DESC")
    List<User> getUserInfoByName(@Param("userName") String userName);

    @Select("SELECT id, user_name as userName, account, password, role_code as roleCode, " +
            "face_path as facePath, face_feature as faceFeature, remark, create_time as createTime, update_time as updateTime " +
            "FROM user_info WHERE user_name = #{userName} AND account = #{account}")
    List<User> getUserInfoByNameAndNo(@Param("userName") String userName, @Param("account") String account);

    @Select("SELECT id, user_name as userName, account, password, role_code as roleCode, " +
            "face_path as facePath, face_feature as faceFeature, remark, create_time as createTime, update_time as updateTime " +
            "FROM user_info WHERE password = #{password} AND account = #{account}")
    User getUserInfoByAccount(@Param("password") String password, @Param("account") String account);

    @Insert("INSERT INTO user_info (user_name, account, password, role_code, face_path, face_feature, remark, create_time, update_time) " +
            "VALUES (#{userName}, #{account}, #{password}, #{roleCode}, #{facePath}, #{faceFeature}, #{remark}, datetime('now'), datetime('now'))")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertUserInfo(User user);

    @Update("UPDATE user_info SET user_name = #{userName}, account = #{account}, password = #{password}, " +
            "role_code = #{roleCode}, face_path = #{facePath}, face_feature = #{faceFeature}, remark = #{remark}, " +
            "update_time = datetime('now') WHERE id = #{id}")
    void updateUserInfo(User user);

    @Delete("DELETE FROM user_info WHERE id = #{id}")
    void deleteUserInfo(@Param("id") Integer id);
}
