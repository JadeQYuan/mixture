package cn.domain.service;

import com.arcsoft.face.*;
import com.arcsoft.face.enums.CompareModel;
import com.arcsoft.face.enums.DetectModel;
import com.arcsoft.face.enums.ErrorInfo;
import com.arcsoft.face.toolkit.ImageInfo;
import com.arcsoft.face.toolkit.ImageInfoEx;
import cn.domain.exception.AppException;
import cn.domain.util.ArcfaceUtils;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ArcFaceService {

    @Autowired
    private FaceEngine faceEngine;

    /**
     * 人脸检测
     *
     * @param imgPath
     * @return
     */
    public List<FaceInfo> detectFace(String imgPath) {
        List<FaceInfo> faceInfo = detectFace(ArcfaceUtils.packImageInfoEx(new File(imgPath)).getImageInfoEx());
        return faceInfo;
    }

    /**
     * 特征提取
     */
    public FaceFeature extractFaceFeature(String imgPath) {
        List<FaceInfo> faceInfo = detectFace(ArcfaceUtils.packImageInfoEx(new File(imgPath)).getImageInfoEx());
        FaceFeature faceFeature = extractFaceFeature(faceInfo, ArcfaceUtils.packImageInfoEx(new File(imgPath)).getImageInfoEx());
        return faceFeature;
    }

    /**
     * 特征比对
     */
    public FaceSimilar compareFaceFeature(String imgPath1, String imgPath2) {

        List<FaceInfo> faceInfo1 = detectFace(ArcfaceUtils.packImageInfoEx(new File(imgPath1)).getImageInfoEx());
        FaceFeature faceFeature1 = extractFaceFeature(faceInfo1, ArcfaceUtils.packImageInfoEx(new File(imgPath1)).getImageInfoEx());

        List<FaceInfo> faceInfo2 = detectFace(ArcfaceUtils.packImageInfoEx(new File(imgPath2)).getImageInfoEx());
        FaceFeature faceFeature2 = extractFaceFeature(faceInfo2, ArcfaceUtils.packImageInfoEx(new File(imgPath2)).getImageInfoEx());

        FaceSimilar faceSimilar = compareFaceFeature(faceFeature1, faceFeature2, CompareModel.LIFE_PHOTO);
        // 相似度
        float score = faceSimilar.getScore();
        log.info("当前匹配的身份证信息【{}】,相似度:{}", "", score);
        return faceSimilar;
    }

    /**
     * 人脸检测
     */
    public List<FaceInfo> detectFace(ImageInfoEx imageInfoEx) {
        if (imageInfoEx == null) {
            return null;
        }
        List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
        int i = faceEngine.detectFaces(imageInfoEx, DetectModel.ASF_DETECT_MODEL_RGB, faceInfoList);
        checkEngineResult(i, ErrorInfo.MOK.getValue(), "人脸检测失败");
        return faceInfoList;
    }

    /**
     * 特征提取
     */
    public FaceFeature extractFaceFeature(List<FaceInfo> faceInfoList, ImageInfoEx imageInfoEx) {

        if (CollectionUtils.isEmpty(faceInfoList) || imageInfoEx == null) {
            log.error("faceInfoList or imageInfoEx is null");
            return null;
        }
        FaceFeature faceFeature = new FaceFeature();
        int i = faceEngine.extractFaceFeature(imageInfoEx, faceInfoList.get(0), faceFeature);
        checkEngineResult(i, ErrorInfo.MOK.getValue(), "人脸特征提取失败");
        return faceFeature;
    }

    /**
     * 特征比对
     */
    public FaceSimilar compareFaceFeature(FaceFeature target, FaceFeature source, CompareModel compareModel) {
        FaceSimilar faceSimilar = new FaceSimilar();
        int i = faceEngine.compareFaceFeature(target, source, compareModel, faceSimilar);
        checkEngineResult(i, ErrorInfo.MOK.getValue(), "人脸特征对比失败");
        return faceSimilar;
    }

    /**
     * 开启人脸属性检测
     *
     * @return
     */
    public void process(ImageInfoEx imageInfo, List<FaceInfo> faceInfoList) {
        // 人脸属性检测
        FunctionConfiguration configuration = new FunctionConfiguration();
        configuration.setSupportAge(true);
        configuration.setSupportFace3dAngle(true);
        configuration.setSupportGender(true);
        configuration.setSupportLiveness(true);
        int process = faceEngine.process(imageInfo, faceInfoList, configuration);
        log.info("开启人脸属性检测：{}", process);
    }

    /**
     * 性别
     *
     * @return
     */
    public int getGender(ImageInfoEx imageInfo) {
        List<FaceInfo> faceInfoList = detectFace(imageInfo);
        process(imageInfo, faceInfoList);
        // 性别检测
        List<GenderInfo> genderInfoList = new ArrayList<GenderInfo>();
        int errorCode = faceEngine.getGender(genderInfoList);
        log.info("性别：{}", genderInfoList.get(0).getGender());
        return genderInfoList.get(0).getGender();
    }

    /**
     * 年龄
     *
     * @return
     */
    public int getAge(ImageInfoEx imageInfo) {
        List<FaceInfo> faceInfoList = detectFace(imageInfo);
        process(imageInfo, faceInfoList);
        //年龄检测
        List<AgeInfo> ageInfoList = new ArrayList<AgeInfo>();
        int errorCode = faceEngine.getAge(ageInfoList);
        log.info("年龄：{}", ageInfoList.get(0).getAge());
        return ageInfoList.get(0).getAge();
    }

    /**
     * IR活体检测
     * IR活体值，未知=-1 、非活体=0 、活体=1、超出人脸=-2
     *
     * @return
     */
    public List<IrLivenessInfo> getIRLiveness(ImageInfo imageInfoGray) {
        // 设置活体测试 阈值
        int errorCode1 = faceEngine.setLivenessParam(0.5f, 0.7f);

        //IR属性处理
        List<FaceInfo> faceInfoListGray = new ArrayList<FaceInfo>();
        int errorCode = faceEngine.detectFaces(imageInfoGray.getImageData(), imageInfoGray.getWidth(), imageInfoGray.getHeight(), imageInfoGray.getImageFormat(), faceInfoListGray);

        FunctionConfiguration configuration2 = new FunctionConfiguration();
        configuration2.setSupportIRLiveness(true);
        errorCode = faceEngine.processIr(imageInfoGray.getImageData(), imageInfoGray.getWidth(), imageInfoGray.getHeight(), imageInfoGray.getImageFormat(), faceInfoListGray, configuration2);
        //IR活体检测
        List<IrLivenessInfo> irLivenessInfo = new ArrayList<>();
        errorCode = faceEngine.getLivenessIr(irLivenessInfo);

        for (IrLivenessInfo livenessInfo : irLivenessInfo) {
            log.info("IR活体：{}", livenessInfo.getLiveness());
        }
        return irLivenessInfo;
    }

    /**
     * RGB活体检测
     * RGB活体值，未知=-1 、非活体=0 、活体=1、超出人脸=-2
     *
     * @return
     */
    public int getRGBLiveness(ImageInfo imageInfoGray) {
        // 设置活体测试 阈值
        int errorCode1 = faceEngine.setLivenessParam(0.5f, 0.7f);

        //IR属性处理
        List<FaceInfo> faceInfoListGray = new ArrayList<FaceInfo>();
        int errorCode = faceEngine.detectFaces(imageInfoGray.getImageData(), imageInfoGray.getWidth(), imageInfoGray.getHeight(), imageInfoGray.getImageFormat(), faceInfoListGray);

        FunctionConfiguration configuration2 = new FunctionConfiguration();
        configuration2.setSupportIRLiveness(true);
        errorCode = faceEngine.processIr(imageInfoGray.getImageData(), imageInfoGray.getWidth(), imageInfoGray.getHeight(), imageInfoGray.getImageFormat(), faceInfoListGray, configuration2);
        //IR活体检测
        List<IrLivenessInfo> irLivenessInfo = new ArrayList<>();
        errorCode = faceEngine.getLivenessIr(irLivenessInfo);

        log.info("IR活体：{}", irLivenessInfo.get(0).getLiveness());
        return irLivenessInfo.get(0).getLiveness();
    }

    /**
     * 3D信息检测
     *
     * @return
     */
    public Face3DAngle getFace3DAngle(ImageInfoEx imageInfo) {
        List<FaceInfo> faceInfoList = detectFace(imageInfo);
        process(imageInfo, faceInfoList);

        List<Face3DAngle> face3DAngleList = new ArrayList<Face3DAngle>();
        int errorCode = faceEngine.getFace3DAngle(face3DAngleList);
        Face3DAngle face3DAngle = face3DAngleList.get(0);
        log.info("3D角度：{}, {}, {}", face3DAngle.getPitch(), face3DAngle.getRoll(), + face3DAngle.getYaw());

        return face3DAngle;
    }

    /**
     * 错误检测
     */
    private void checkEngineResult(int errorCode, int sourceCode, String errMsg) {
        if (errorCode != sourceCode) {
            throw new AppException(errMsg);
        }

    }

    /**
     *
     * 本地摄像头视频进行人脸识别
     */
    public void faceRecognition() throws Exception, InterruptedException{

        //new一个摄像头窗口
        log.info("开始新建摄像头窗口");
        String canasName = "摄像头窗口";
        CanvasFrame canvas = new CanvasFrame(canasName);
        /**
         * DO_NOTHING_ON_CLOSE，（在你点击关闭按钮的时候，不会被关闭，）不执行任何操作。
         *
         * HIDE_ON_CLOSE，(当你点击关闭按钮的时候，不会释放内存，只是隐藏该界面，没有真正的关闭，还占有资源)只隐藏界面，setVisible(false)。
         *
         * EXIT_ON_CLOSE,直接关闭应用程序，System.exit(0)。一个main函数对应一整个程序。
         *
         * EXIT_ON_CLOSE,直接关闭应用程序，System.exit(0)。一个main函数对应一整个程序。
         */
        log.info("窗口设置");
        canvas.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //使用OpenCVFrameGrabber获取摄像头画面，0代表当前设备的序号，一般都是0
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);

        try {

            //开始获取摄像头数据
            grabber.start();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "摄像头准备失败:请检查摄像头是否正确接入!", "提示:", JOptionPane.WARNING_MESSAGE);
            if (grabber != null) {
                grabber.close();
            }

            return;
        }

        //判断窗口是否关闭
        while(canvas.isDisplayable()){
            //获取摄像头图像并在窗口中显示
            canvas.showImage(grabber.grab());
        }
        //调用关闭按钮
        log.info("调用了关闭摄像头按钮");
        grabber.close();


    }

}
