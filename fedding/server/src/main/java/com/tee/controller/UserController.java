package com.tee.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tee.emum.RoleEnum;
import com.tee.pojo.qo.UserQo;
import com.tee.pojo.vo.User;
import com.tee.service.UserService;
import com.tee.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/service/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Value("${user.default.password}")
    private String defaultPassword;

    /**
     * 账号密码登录
     *
     * @param userQo
     * @return
     */
    @PostMapping("/accountLogin")
    public Result accountLogin(@RequestBody UserQo userQo, HttpServletResponse response) {
        String account = userQo.getAccount();
        String password = userQo.getPassword();
        if (StringUtils.isEmpty(account) || StringUtils.isEmpty(password)) {
            return Result.error("账号或密码不能为空");
        }
        List<User> userInfoList = userService.getUserInfoByNameAndNo(null, account);
        if (CollectionUtils.isEmpty(userInfoList)) {
            return Result.error("账号或密码错误");
        }


        User userInfo = userInfoList.get(0);
        String password1 = userInfo.getPassword();
        String decrypt = AESUtil.decrypt(password1);
        if (!password.equals(decrypt)) {
            return Result.error("账号或密码错误");
        }
        String facePath = userInfo.getFacePath();
        if (!StringUtils.isEmpty(facePath)) {
            userInfo.setFacePath(Base64Util.fileToBase64(facePath));
        }
        CookieUtils.setCookie(userInfo.getUserId(), response);

        return Result.success(userInfo);
    }

    /**
     * 用户列表
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/list")
    public Result userList(@RequestParam(value = "userName", required = false) String userName, @RequestParam(value = "pageNo", defaultValue = "1") int pageNo, @RequestParam(value = "size", defaultValue = "10") int size) {
        PageHelper.startPage(pageNo, size);
        List<User> userInfo = userService.getUserInfoByName(userName);
        if (CollectionUtils.isEmpty(userInfo)) {
            return Result.success();
        }
        for (User user : userInfo) {
            String facePath = user.getFacePath();
            if (!StringUtils.isEmpty(facePath)) {
                // 加载图片资源
                user.setFacePath(Base64Util.fileToBase64(facePath));
            }
        }

        PageInfo<User> pageInfo = new PageInfo(userInfo);
        return Result.success(pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
    }

    /**
     * 新增用户
     *
     * @param userQo
     * @return
     */
    @PostMapping("/add")
    public Result addUser(@Valid @RequestBody UserQo userQo) {
        String userName = userQo.getUserName();
        String account = userQo.getAccount();
        List<User> userInfo = userService.getUserInfoByNameAndNo(userName, account);
        if (!CollectionUtils.isEmpty(userInfo)) {
            return Result.error("用户已存在");
        }
        User user = new User();
        BeanUtils.copyProperties(userQo, user);
        user.setPassword(AESUtil.encrypt(defaultPassword));
        String roleName = RoleEnum.getRoleNameByCode(user.getRoleId());
        user.setRoleName(roleName);
        user.setUserId(UIdUtil.generateUUID());
        userService.insertUserInfo(user);

        return Result.success();
    }

    /**
     * 修改密码
     *
     * @param userQo
     * @return
     */
    @PutMapping("/updatePassword")
    public Result updatePassword(@Valid @RequestBody UserQo userQo) {
        String userId = userQo.getUserId();
        List<User> userInfo = userService.getUserInfo(userId);
        if (CollectionUtils.isEmpty(userInfo)) {
            return Result.error("用户不存在");
        }
        String password = userQo.getPassword();
        if (StringUtils.isEmpty(password)) {
            return Result.error("密码不能为空");
        }
        User user = new User();
        user.setPassword(AESUtil.encrypt(password));
        user.setUserId(userId);
        userService.insertUserInfo(user);

        return Result.success();
    }

    /**
     * 修改用户信息
     *
     * @param userQo
     * @return
     */
    @PutMapping("/updateUserInfo")
    public Result updateUserInfo(@Valid @RequestBody UserQo userQo) {
        String userId = userQo.getUserId();
        List<User> userInfo = userService.getUserInfo(userId);
        if (CollectionUtils.isEmpty(userInfo)) {
            return Result.error("用户不存在");
        }

        User user = new User();
        BeanUtils.copyProperties(userQo, user);
        userService.updateUserInfo(user);
        return Result.success();
    }

    /**
     * 删除用户
     *
     * @param userQo
     * @return
     */
    @DeleteMapping("/delete")
    public Result deleteUserInfo(@Valid @RequestBody UserQo userQo) {
        String userId = userQo.getUserId();
        List<User> userInfo = userService.getUserInfo(userId);
        if (CollectionUtils.isEmpty(userInfo)) {
            return Result.error("用户不存在");
        }
        userService.deleteUserInfo(userId);
        return Result.success();
    }



}
