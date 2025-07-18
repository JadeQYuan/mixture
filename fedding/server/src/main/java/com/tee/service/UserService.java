package com.tee.service;

import com.alibaba.fastjson.JSONObject;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.IrLivenessInfo;
import com.arcsoft.face.toolkit.ImageInfo;
import com.arcsoft.face.toolkit.ImageInfoEx;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tee.entity.User;
import com.tee.exception.AppException;
import com.tee.mapper.UserMapper;
import com.tee.pojo.PageVo;
import com.tee.pojo.UserQo;
import com.tee.util.ArcfaceUtils;
import com.tee.util.Base64Util;
import com.tee.util.DateUtil;
import com.tee.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.util.List;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ArcFaceService arcFaceService;

    @Value("${face.score}")
    private float faceScore;

    @Value("${face.path}")
    private String facePath;

    @Value("${face.path.back}")
    private String facePathBack;

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
        User userInfo = userMapper.getUserInfo(userId);
        if (userInfo == null) {
            throw new AppException("用户不存在");
        }
        // 备份照片
        String facePath1 = userInfo.getFacePath();
        if (!StringUtils.isEmpty(facePath1)) {
            File file = new File(facePath1);
            if (file.exists()) {
                String days = DateUtil.getDays();
                File fileBack = new File(new File(facePathBack).getAbsolutePath() + "/back_del_" + days + "_" + userId + ".png");
                if (!fileBack.getParentFile().exists()) {
                    fileBack.getParentFile().mkdirs();
                }
                file.renameTo(fileBack);
            }
        }

        userMapper.deleteUserInfo(userId);
    }

    public void uploadPhoto(MultipartFile multipartFile, Integer id) throws Exception {
        User user = userMapper.getUserInfo(id);
        if (user == null) {
            throw new AppException("用户不存在！");
        }
        ArcfaceUtils.ImageInfoMeta imageInfoMeta = ArcfaceUtils.packImageInfoMeta(multipartFile.getInputStream());
        // 活体检测
//        checkIRLivenessFace(imageInfoMeta.getImageInfo());
        List<FaceInfo> faceInfo = arcFaceService.detectFace(imageInfoMeta.getImageInfoEx());
        if (CollectionUtils.isEmpty(faceInfo)) {
            throw new AppException("人脸校验失败，请调整角度重新拍摄！");
        }
        if (faceInfo.size() > 1) {
            throw new AppException("检测到多张人脸，请保持镜头只有一人！");
        }
        FaceFeature faceFeature = arcFaceService.extractFaceFeature(faceInfo, imageInfoMeta.getImageInfoEx());
        if (faceFeature == null) {
            throw new AppException("人脸特征校验失败，请调整角度重新拍摄！");
        }
        Integer userId = compareFaceFeature(faceFeature, id);
        if (userId != null) {
            throw new AppException("已经存在相似人脸，请调整角度重新拍摄！");
        }
        // 若是修改张片，则将旧张片进行备份
        String facePath1 = user.getFacePath();
        if (!StringUtils.isEmpty(facePath1)) {
            File file = new File(facePath1);
            if (file.exists()) {
                String days = DateUtil.getDays();
                File fileBack = new File(new File(facePathBack).getAbsolutePath() + "/back_" + days + "_" + id + ".png");
                if (!fileBack.getParentFile().exists()) {
                    fileBack.getParentFile().mkdirs();
                }
                file.renameTo(fileBack);
            }
        }

        // 重命名文件，如果目标文件存在则替换
        File fileNew = new File(new File(facePath).getAbsolutePath() + "/" + id + ".png");
        if (fileNew.exists()) {
            fileNew.delete();
        }
        if (!fileNew.getParentFile().exists()) {
            fileNew.getParentFile().mkdirs();
        }
        multipartFile.transferTo(fileNew);
        userMapper.updateFaceInfo(id, fileNew.getPath(), JSONObject.toJSONString(faceFeature));
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
        ArcfaceUtils.ImageInfoMeta imageInfoMeta = ArcfaceUtils.packImageInfoMeta(multipartFile.getInputStream());
        ImageInfoEx imageInfoEx = imageInfoMeta.getImageInfoEx();
        // 活体检测
//        checkIRLivenessFace(imageInfoMeta.getImageInfo());
        List<FaceInfo> faceInfo = arcFaceService.detectFace(imageInfoEx);
        if (CollectionUtils.isEmpty(faceInfo)) {
            throw new AppException("人脸校验失败，请调整角度重新拍摄！");
        }
        if (faceInfo.size() > 1) {
            throw new AppException("检测到多张人脸，请保持镜头只有一人！");
        }
        FaceFeature faceFeature = arcFaceService.extractFaceFeature(faceInfo, imageInfoEx);
        // int liveness = arcFaceService.getLiveness(imageInfoEx); // 活体检测

        Integer userId = compareFaceFeature(faceFeature, null);
        if (StringUtils.isEmpty(userId)) {
            throw new AppException("用户不存在或者识别失败！");
        }
        User userInfo = userMapper.getUserInfo(userId);
        if (userInfo == null) {
            throw new AppException("用户不存在");
        }
        return TokenUtil.generateToken(userId.toString());
    }

    private Integer compareFaceFeature(FaceFeature faceFeature, Integer id) {
        Integer userId = null;
        List<User> userInfoList = userMapper.getUserInfoByName(null, null);
        for (User user : userInfoList) {
            Integer idTemp = user.getId();
            if (id != null && id == idTemp) {
                continue;
            }
            try {
                String faceFeatureUser = user.getFaceFeature();
                if (StringUtils.isEmpty(faceFeatureUser)) {
                    continue;
                }
                FaceFeature faceFeatureTemp = JSONObject.parseObject(faceFeatureUser, FaceFeature.class);
                String facePath = user.getFacePath();
                File fileTemp = new File(facePath);
                if (!fileTemp.exists()) {
                    continue;
                }
                com.arcsoft.face.FaceSimilar faceSimilar = arcFaceService.compareFaceFeature(faceFeature, faceFeatureTemp, com.arcsoft.face.enums.CompareModel.LIFE_PHOTO);
                float score = faceSimilar.getScore();
                if (score > faceScore) {
                    userId = user.getId();
                    break;
                }
            } catch (Exception e) {
                log.error("加载人脸失败！, 用户id是：{},{}", user.getId(), e);
            }

        }
        return userId;
    }

    /**
     * IR活体检测
     *
     * @param imageInfoGray
     */
    private void checkIRLivenessFace(ImageInfo imageInfoGray) {
        List<IrLivenessInfo> irLiveness = arcFaceService.getIRLiveness(imageInfoGray);
        if (CollectionUtils.isEmpty(irLiveness)) {
            throw new AppException("人脸校验失败,非活体，请调整角度重新拍摄！");
        }
        if (irLiveness.size() > 1) {
            throw new AppException("检测到多张人脸，请保持镜头只有一人！");
        }
        int irCode = irLiveness.get(0).getLiveness();
        if (irCode != 1) {
            throw new AppException("人脸校验失败,非活体，请调整角度重新拍摄！");
        }
    }
}
