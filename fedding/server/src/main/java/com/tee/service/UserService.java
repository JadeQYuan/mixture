package com.tee.service;

import com.alibaba.fastjson.JSONObject;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.toolkit.ImageInfoEx;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tee.entity.User;
import com.tee.exception.AppException;
import com.tee.mapper.UserMapper;
import com.tee.pojo.PageVo;
import com.tee.pojo.UserQo;
import com.tee.util.Base64Util;
import com.tee.util.TokenUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ArcFaceService arcFaceService;

    public User getCurrentUserInfo() {
        Integer userId = TokenUtil.getToken();
        if (userId == null) {
            throw new AppException("未登录");
        }
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
        return userMapper.getUserInfo(userId);
    }

    public PageVo<User> getUserList(UserQo userQo) {
        PageHelper.startPage(userQo.getPageNo(), userQo.getPageSize());
        Page<User> userList = (Page<User>)userMapper.getUserInfoByName(userQo.getUserName(), userQo.getAccount());
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
        String account = user.getAccount();
        int count = userMapper.checkUserByAccount(account);
        if (count > 0) {
            throw new AppException("工号已存在");
        }
        userMapper.insertUserInfo(user);
        return user;
    }

    public void updatePassword(User user) {
        if (user.getId() == null || StringUtils.isEmpty(user.getPassword())) {
            throw new AppException("用户ID和密码不能为空");
        }
        userMapper.updatePassword(user);
    }

    public void updateUserInfo(User user) {
        User userInfo = userMapper.getUserInfo(user.getId());
        if (userInfo == null) {
            throw new AppException("用户不存在");
        }
        int count = userMapper.checkUserByAccount(user.getAccount());
        if (count > 0) {
            throw new AppException("工号已存在");
        }
        userMapper.updateUserInfo(user);
    }

    public void deleteUserInfo(Integer userId) {
        userMapper.deleteUserInfo(userId);
    }

    public void uploadPhoto(MultipartFile multipartFile, Integer id) throws Exception {
        User user = userMapper.getUserInfo(id);
        if (user == null) {
            throw new AppException("用户不存在！");
        }
        File file1 = new File("/face/");
        File file = new File(file1.getAbsolutePath() + "/" + id + ".png");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        multipartFile.transferTo(file);
        List<com.arcsoft.face.FaceInfo> faceInfo = arcFaceService.detectFace(com.tee.util.ArcfaceUtils.packImageInfoEx(file).getImageInfoEx());
        if (CollectionUtils.isEmpty(faceInfo)) {
            file.delete();
            throw new AppException("人脸校验失败，请调整角度重新拍摄！");
        }
        FaceFeature faceFeature = arcFaceService.extractFaceFeature(faceInfo, com.tee.util.ArcfaceUtils.packImageInfoEx(file).getImageInfoEx());
        if (faceFeature == null) {
            file.delete();
            throw new AppException("人脸特征校验失败，请调整角度重新拍摄！");
        }
        userMapper.updateFaceInfo(id, file.getPath(), JSONObject.toJSONString(faceFeature));
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
        return TokenUtil.generateToken(userInfo.getId().toString());
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
