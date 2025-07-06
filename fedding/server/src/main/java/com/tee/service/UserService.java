package com.tee.service;

import com.tee.mapper.UserMapper;
import com.tee.pojo.vo.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    public List<User> getUserInfo(String userId) {
        return userMapper.getUserInfo(userId);
    }

    public List<User> getUserInfoByName(String userName) {
        return userMapper.getUserInfoByName(userName);
    }

    public List<User> getUserInfoByAccount(String password, String account) {
        return userMapper.getUserInfoByAccount(password, account);
    }

    public List<User> getUserInfoByNameAndNo(String userName, String account) {
        return userMapper.getUserInfoByNameAndNo(userName, account);
    }

    public void insertUserInfo(User user) {
        userMapper.insertUserInfo(user);
    }

    public void updateUserInfo(User user) {
        userMapper.updateUserInfo(user);
    }

    public void deleteUserInfo(String userId) {
        userMapper.deleteUserInfo(userId);
    }
}
