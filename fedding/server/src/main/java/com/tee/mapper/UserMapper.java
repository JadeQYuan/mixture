package com.tee.mapper;

import com.tee.pojo.vo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserMapper {

    List<User> getUserInfo(@Param("userId") String userId);

    List<User> getUserInfoByName(@Param("userName") String userName);

    List<User> getUserInfoByNameAndNo(@Param("userName") String userName, @Param("account") String account);

    List<User> getUserInfoByAccount(@Param("password") String password, @Param("account") String account);

    void insertUserInfo(User user);

    void updateUserInfo(User user);

    void deleteUserInfo(@Param("userId") String userId);



}
