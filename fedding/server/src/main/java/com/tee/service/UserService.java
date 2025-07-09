package com.tee.service;

import com.tee.constant.Contants;
import com.tee.mapper.UserMapper;
import com.tee.pojo.vo.User;
import com.tee.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    HttpServletRequest httpServletRequest;

    public User getCurrentUser() {
        String authorization = httpServletRequest.getHeader(Contants.AUTHORIZATION);
        if (authorization == null) {
            return null;
        }
        String userId = JwtUtils.getSubject(authorization);
        return getUserInfo(userId).get(0);
    }

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
