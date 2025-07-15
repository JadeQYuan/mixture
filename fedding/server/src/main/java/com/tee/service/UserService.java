package com.tee.service;

import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.toolkit.ImageInfoEx;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tee.constant.Contants;
import com.tee.entity.User;
import com.tee.exception.AppException;
import com.tee.mapper.UserMapper;
import com.tee.pojo.PageQo;
import com.tee.pojo.PageVo;
import com.tee.util.Base64Util;
import com.tee.util.JwtUtils;
import com.tee.util.TokenUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Collections;
import java.util.List;

@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    private ArcFaceService arcFaceService;
    public User getCurrentUserInfo() {
        String authorization = httpServletRequest.getHeader(Contants.AUTHORIZATION);
        if (authorization == null) {
            throw new AppException("未登录");
        }
        Integer userId = TokenUtil.getToken();
        User userInfo = userMapper.getUserInfo(userId);
        if (userInfo == null) {
            throw new AppException("用户不存在");
        }
        String facePath = userInfo.getFacePath();
        if (!StringUtils.isEmpty(facePath)) {
            userInfo.setFacePath(Base64Util.fileToBase64(facePath));
        }
        return userInfo;
    }

    public User getById(Integer userId) {
        User userInfo = userMapper.getUserInfo(userId);
        return userInfo;
    }

    public PageVo<User> getUserList(String userName, String account, PageQo pageQo) {
        PageHelper.startPage(pageQo.getPageNo(), pageQo.getPageSize());
        Page<User> userList = (Page<User>)userMapper.getUserInfoByName(userName);
        if (CollectionUtils.isEmpty(userList)) {
            return new PageVo<>();
        }
        for (User user : userList) {
            String facePath = user.getFacePath();
            if (!StringUtils.isEmpty(facePath)) {
                user.setFacePath(Base64Util.fileToBase64(facePath));
            }
        }
        return new PageVo<>(userList);
    }

    public User addUser(User user) {
        String userName = user.getUserName();
        String account = user.getAccount();
        List<User> userInfo = userMapper.getUserInfoByNameAndNo(userName, account);
        if (!CollectionUtils.isEmpty(userInfo)) {
            throw new AppException("用户已存在");
        }
//        User user = new User();
//        BeanUtils.copyProperties(user, user);
        userMapper.insertUserInfo(user);
        return user;
    }

    public void updatePassword(User user) {
//        Integer userId = user.getId();
//        List<User> userInfo = userMapper.getUserInfo(userId);
//        if (CollectionUtils.isEmpty(userInfo)) {
//            throw new AppException("用户不存在");
//        }
//        String password = userQo.getPassword();
//        if (StringUtils.isEmpty(password)) {
//            throw new AppException("密码不能为空");
//        }
//        User user = new User();
//        user.setPassword(password);
//        userMapper.updateUserInfo(user);
    }

    public void updateUserInfo(User user) {
//        String userId = userQo.getUserId();
//        List<User> userInfo = userMapper.getUserInfo(userId);
//        if (CollectionUtils.isEmpty(userInfo)) {
//            throw new AppException("用户不存在");
//        }
//        User user = new User();
//        BeanUtils.copyProperties(userQo, user);
//        userMapper.updateUserInfo(user);
    }

    public void deleteUserInfo(Integer userId) {
        userMapper.deleteUserInfo(userId);
    }

    public void uploadPhoto(MultipartFile multipartFile, Integer userId) throws Exception {
        User user = userMapper.getUserInfo(userId);
        if (user == null) {
            throw new AppException("用户不存在！");
        }
        File file1 = new File("/face/");
        File file = new File(file1.getAbsolutePath() + "/" + userId + ".png");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        multipartFile.transferTo(file);
        List<com.arcsoft.face.FaceInfo> faceInfo = arcFaceService.detectFace(com.tee.util.ArcfaceUtils.packImageInfoEx(file).getImageInfoEx());
        if (CollectionUtils.isEmpty(faceInfo)) {
            file.delete();
            throw new AppException("人脸校验失败，请调整角度重新拍摄！");
        }
        com.arcsoft.face.FaceFeature faceFeature = arcFaceService.extractFaceFeature(faceInfo, com.tee.util.ArcfaceUtils.packImageInfoEx(file).getImageInfoEx());
        if (faceFeature == null) {
            file.delete();
            throw new AppException("人脸特征校验失败，请调整角度重新拍摄！");
        }
//        face.setUserId(userId);
//        face.setUserName(user.getUserName());
//        face.setFacePath(file.getPath());
//        face.setFaceFeature(com.alibaba.fastjson.JSONObject.toJSONString(faceFeature));
//        List<com.tee.entity.Face> faceInfo1 = faceService.getFaceInfo(userId);
//        if (CollectionUtils.isEmpty(faceInfo1)) {
//            faceService.insertFaceInfo(face);
//        } else {
//            faceService.updateFaceInfo(face);
//        }
    }

    public String loginByAccount(User user) {
        String account = user.getAccount();
        String password = user.getPassword();
        if (StringUtils.isEmpty(account) || StringUtils.isEmpty(password)) {
            throw new AppException("账号或密码不能为空");
        }
        User userInfo = userMapper.getUserInfoByAccount(user.getPassword(), account);
        if (userInfo == null) {
            throw new AppException("账号或密码错误");
        }
        String jwtToken = JwtUtils.generateToken(userInfo.getId().toString());
        return jwtToken;
    }

    public String loginByFace(MultipartFile multipartFile) throws Exception {
        File file1 = new File("/face/");
        File file = new File(file1.getAbsolutePath() + "/face_temp.png");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        multipartFile.transferTo(file);
        ImageInfoEx imageInfoEx1 = com.tee.util.ArcfaceUtils.packImageInfoEx(file).getImageInfoEx();
        List<com.arcsoft.face.FaceInfo> faceInfo = arcFaceService.detectFace(imageInfoEx1);
        if (CollectionUtils.isEmpty(faceInfo)) {
            throw new AppException("人脸校验失败，请调整角度重新拍摄！");
        }
        FaceFeature faceFeature = arcFaceService.extractFaceFeature(faceInfo, imageInfoEx1);
        int liveness = arcFaceService.getLiveness(imageInfoEx1); // 活体检测
//        List<com.tee.entity.Face> faceInfoList = faceService.getFaceInfo(null);
//        String userId = null;
//        for (com.tee.entity.Face face : faceInfoList) {
//            String facePath = face.getFacePath();
//            if (StringUtils.isEmpty(facePath)) {
//                continue;
//            }
//            File fileTemp = new File(facePath);
//            ImageInfoEx imageInfoEx = com.tee.util.ArcfaceUtils.packImageInfoEx(fileTemp).getImageInfoEx();
//            List<com.arcsoft.face.FaceInfo> faceInfoTemp = arcFaceService.detectFace(imageInfoEx);
//            com.arcsoft.face.FaceFeature faceFeatureTemp = arcFaceService.extractFaceFeature(faceInfoTemp, imageInfoEx);
//            com.arcsoft.face.FaceSimilar faceSimilar = arcFaceService.compareFaceFeature(faceFeature, faceFeatureTemp, com.arcsoft.face.enums.CompareModel.LIFE_PHOTO);
//            float score = faceSimilar.getScore();
//            if (score > 0.75) {
//                userId = face.getUserId();
//                break;
//            }
//        }
//        if (StringUtils.isEmpty(userId)) {
//            throw new AppException("用户不存在或者识别失败！");
//        }
//        List<User> userInfo = userMapper.getUserInfo(userId);
//        if (CollectionUtils.isEmpty(userInfo)) {
//            throw new AppException("用户不存在");
//        }
//        String jwtToken = JwtUtils.generateToken(userId);
        return "jwtToken";
    }
}
