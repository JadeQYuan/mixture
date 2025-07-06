package com.tee.controller;

import com.alibaba.fastjson.JSONObject;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.enums.CompareModel;
import com.arcsoft.face.toolkit.ImageInfoEx;
import com.tee.pojo.vo.Face;
import com.tee.pojo.vo.User;
import com.tee.service.ArcFaceService;
import com.tee.service.FaceService;
import com.tee.service.UserService;
import com.tee.service.caffeine.CacheClient;
import com.tee.util.ArcfaceUtils;
import com.tee.util.JwtUtils;
import com.tee.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/service/face")
public class FaceController {

    @Autowired
    private ArcFaceService arcFaceService;

    @Autowired
    private FaceService faceService;

    @Autowired
    private UserService userService;

    @Autowired
    private CacheClient cacheClient;

    /**
     * 人脸检测
     *
     * @param multipartFile
     * @return
     * @throws Exception
     */
    @PostMapping("/faceDetection")
    public Result faceDetection(@RequestParam(value = "imageFile") MultipartFile multipartFile) throws Exception{
        File file1 = new File("/face/");
        File file = new File(file1.getAbsolutePath() + "/face_temp.png");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        multipartFile.transferTo(file);
        List<FaceInfo> faceInfo = arcFaceService.detectFace(ArcfaceUtils.packImageInfoEx(file).getImageInfoEx());

        if (CollectionUtils.isEmpty(faceInfo)) {
            return Result.error("人脸校验失败，请调整角度重新拍摄！");
        }
        FaceFeature faceFeature = arcFaceService.extractFaceFeature(faceInfo, ArcfaceUtils.packImageInfoEx(file).getImageInfoEx());

//        int gender = arcFaceService.getGender(ArcfaceUtils.packImageInfoEx(file).getImageInfoEx());
//        int age = arcFaceService.getAge(ArcfaceUtils.packImageInfoEx(file).getImageInfoEx());
        int liveness = arcFaceService.getLiveness(ArcfaceUtils.packImageInfoEx(file).getImageInfoEx()); // 活体检测
        //Face3DAngle face3DAngle = arcFaceService.getFace3DAngle(ArcfaceUtils.packImageInfoEx(file).getImageInfoEx()); // 3D信息检测

        return Result.success(faceFeature);
    }

    /**
     * 人脸照片 上传
     *
     * @param multipartFile
     * @return
     * @throws Exception
     */
    @PostMapping ("/faceUpload")
    public Result faceUpload(@RequestParam(value = "imageFile") MultipartFile multipartFile,
        @RequestParam(value = "userId") String userId) throws Exception{
        List<User> userInfo = userService.getUserInfo(userId);
        if (CollectionUtils.isEmpty(userInfo)) {
            return Result.error("用户不存在！");
        }

        File file1 = new File("/face/");
        File file = new File(file1.getAbsolutePath() + "/" + userId + ".png");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        multipartFile.transferTo(file);
        List<FaceInfo> faceInfo = arcFaceService.detectFace(ArcfaceUtils.packImageInfoEx(file).getImageInfoEx());
        if (CollectionUtils.isEmpty(faceInfo)) {
            file.delete();
            return Result.error("人脸校验失败，请调整角度重新拍摄！");
        }
        FaceFeature faceFeature = arcFaceService.extractFaceFeature(faceInfo, ArcfaceUtils.packImageInfoEx(file).getImageInfoEx());
        if (faceFeature == null) {
            file.delete();
            return Result.error("人脸特征校验失败，请调整角度重新拍摄！");
        }
        User user = userInfo.get(0);
        Face face = new Face();
        face.setUserId(userId);
        face.setUserName(user.getUserName());
        face.setFacePath(file.getPath());
        face.setFaceFeature(JSONObject.toJSONString(faceFeature));

        List<Face> faceInfo1 = faceService.getFaceInfo(userId);
        if (CollectionUtils.isEmpty(faceInfo1)) {
            // 入库
            faceService.insertFaceInfo(face);
        } else {
            faceService.updateFaceInfo(face);
        }


        // 存入缓存
        // cacheClient.set("face_" + userId, faceFeature);

        return Result.success();
    }

    /**
     * 人脸登录认证
     *
     * @param multipartFile
     * @return
     * @throws Exception
     */
    @PostMapping("/faceLogin")
    public Result faceLogin(@RequestParam(value = "imageFile") MultipartFile multipartFile, HttpServletResponse response) throws Exception{
        File file1 = new File("/face/");
        File file = new File(file1.getAbsolutePath() + "/face_temp.png");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        multipartFile.transferTo(file);
        ImageInfoEx imageInfoEx1 = ArcfaceUtils.packImageInfoEx(file).getImageInfoEx();
        List<FaceInfo> faceInfo = arcFaceService.detectFace(imageInfoEx1);

        if (CollectionUtils.isEmpty(faceInfo)) {
            return Result.error("人脸校验失败，请调整角度重新拍摄！");
        }
        FaceFeature faceFeature = arcFaceService.extractFaceFeature(faceInfo, imageInfoEx1);

//        int gender = arcFaceService.getGender(imageInfoEx1);
//        int age = arcFaceService.getAge(imageInfoEx1);
        int liveness = arcFaceService.getLiveness(imageInfoEx1); // 活体检测
        //Face3DAngle face3DAngle = arcFaceService.getFace3DAngle(imageInfoEx1); // 3D信息检测

        // 查询库中所有人脸信息做比对
        List<Face> faceInfoList = faceService.getFaceInfo(null);
        String userId = null;
        for (Face face : faceInfoList) {
            String facePath = face.getFacePath();
            if (StringUtils.isEmpty(facePath)) {
                continue;
            }
            File fileTemp = new File(facePath);
            ImageInfoEx imageInfoEx = ArcfaceUtils.packImageInfoEx(fileTemp).getImageInfoEx();
            List<FaceInfo> faceInfoTemp = arcFaceService.detectFace(imageInfoEx);
            FaceFeature faceFeatureTemp = arcFaceService.extractFaceFeature(faceInfoTemp, imageInfoEx);
            FaceSimilar faceSimilar = arcFaceService.compareFaceFeature(faceFeature, faceFeatureTemp, CompareModel.LIFE_PHOTO);
            // 相似度
            float score = faceSimilar.getScore();
            if (score > 0.75) {
                userId = face.getUserId();
                log.info("当前匹配的身份证信息【{}】, 相似度:{}", face.getUserName(), score);
                break;
            }
        }
        if (StringUtils.isEmpty(userId)) {
            return Result.error("用户不存在或者识别失败！");
        }
        List<User> userInfo = userService.getUserInfo(userId);
        if (CollectionUtils.isEmpty(userInfo)) {
            return Result.error("用户不存在");
        }

        String jwtToken = JwtUtils.generateToken(userId);
        Cookie authCookie = new Cookie("authToken", jwtToken); // 将JWT设置为Cookie的值
        authCookie.setHttpOnly(true); // 防止客户端脚本访问Cookie
        authCookie.setPath("/"); // 设置路径为根路径
        response.addCookie(authCookie); // 将Cookie添加到响应中

        return Result.success(userInfo.get(0));
    }



    /**
     * 特征提取
     */
    @GetMapping("/extractFaceFeature")
    public Result extractFaceFeature(String imgPath) {
        FaceFeature faceFeature = arcFaceService.extractFaceFeature(imgPath);
        return Result.success(faceFeature);
    }

    /**
     * 特征比对
     */
    @GetMapping("/compareFaceFeature")
    public Result compareFaceFeature(String imgPath1, String imgPath2) {
        FaceSimilar faceSimilar = arcFaceService.compareFaceFeature(imgPath1, imgPath2);
        // 相似度
        float score = faceSimilar.getScore();
        log.info("当前匹配的身份证信息【{}】,相似度:{}", "", score);
        return Result.success(faceSimilar);
    }

    /**
     * 本地摄像头视频进行人脸识别
     */
    @GetMapping("/faceRecognition")
    public Result faceRecognition() throws Exception {
        arcFaceService.faceRecognition();
        return Result.success();
    }


}
