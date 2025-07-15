package com.tee.controller;

import com.github.pagehelper.Page;
import com.tee.entity.User;
import com.tee.pojo.PageQo;
import com.tee.pojo.PageVo;
import com.tee.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户信息管理
 *
 */
@Slf4j
@RestController
@RequestMapping("/service/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/info")
    public User getUserInfo() {
        return userService.getCurrentUserInfo();
    }

    /**
     * 用户列表
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/list")
    public PageVo<User> userList(String userName, String account, PageQo pageQo) {
        return userService.getUserList(userName, account, pageQo);
    }

    /**
     * 新增用户
     *
     * @param user
     * @return
     */
    @PostMapping("/add")
    public User addUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    /**
     * 修改密码
     *
     * @param user
     * @return
     */
    @PutMapping("/updatePassword")
    public void updatePassword(@RequestBody User user) {
        userService.updatePassword(user);
    }

    /**
     * 修改用户信息
     *
     * @param user
     * @return
     */
    @PutMapping("/updateUserInfo")
    public void updateUserInfo(@Valid @RequestBody User user) {
        userService.updateUserInfo(user);
    }

    /**
     * 删除用户
     *
     * @param user
     * @return
     */
    @DeleteMapping("/delete")
    public void deleteUserInfo(@RequestBody User user) {
        userService.deleteUserInfo(user.getId());
    }

    /**
     * 用户照片上传/更新
     */
    @PostMapping("/uploadPhoto")
    public void uploadPhoto(@RequestParam(value = "imageFile") MultipartFile multipartFile, @RequestParam Integer id) throws Exception {
        userService.uploadPhoto(multipartFile, id);
    }


}
