package cn.domain.config;

import com.arcsoft.face.EngineConfiguration;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FunctionConfiguration;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectOrient;
import com.arcsoft.face.enums.ErrorInfo;
import cn.domain.constant.Contants;
import cn.domain.exception.AppException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Slf4j
@Data
@Configuration
public class ArcSoftConfig {

    @Value("${face.appId}")
    private String appid;

    // win平台sdk
    @Value("${face.windows.sdkKey}")
    private String winsdkkey;


    // linux平台sdk
    private String linuxsdkkey;


    // dll/so库路径
    @Value("${face.libpath}")
    private String libpath;

    /**
     * 装载FaceEngine交给spring托管
     *
     * @return
     */
    @Bean
    public FaceEngine faceEngine() {
        /*String sdkkey = "";
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            sdkkey = winsdkkey;
            String projectPath = System.getProperty("user.dir");
            libpath = projectPath + "\\WIN64";
        } else {
            sdkkey = linuxsdkkey;
        }*/
        libpath = new File("").getAbsolutePath() + libpath;
        FaceEngine faceEngine = new FaceEngine(libpath);
        int errorCode = faceEngine.activeOnline(appid, winsdkkey);
        if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
            log.error("引擎注册失败");
            throw new AppException("引擎注册失败");
        }
        EngineConfiguration engineConfiguration = getFaceEngineConfiguration();
        // 初始化引擎
        errorCode = faceEngine.init(engineConfiguration);
        if (errorCode != ErrorInfo.MOK.getValue()) {
            log.error("初始化引擎失败");
            throw new AppException("初始化引擎失败");
        }
        return faceEngine;
    }

    /**
     * 初始化引擎配置
     *
     * @return
     */
    private EngineConfiguration getFaceEngineConfiguration() {
        EngineConfiguration engineConfiguration = new EngineConfiguration();
        // 配置引擎模式
        if ("IMAGE".equals(Contants.DETECT_MODE)) {
            engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);
        } else {
            engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_VIDEO);
        }
        // 配置人脸角度 全角度 ASF_OP_ALL_OUT 不够准确且检测速度慢
        switch (Contants.DETECT_FACE_ORIENT_PRIORITY) {
            case "ASF_OP_0_ONLY":
                engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_0_ONLY);
                break;
            case "ASF_OP_90_ONLY":
                engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_90_ONLY);
                break;
            case "ASF_OP_270_ONLY":
                engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_270_ONLY);
                break;
            case "ASF_OP_180_ONLY":
                engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_180_ONLY);
                break;
            case "ASF_OP_ALL_OUT":
                engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_ALL_OUT);
                break;
            default:
                engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_ALL_OUT);
        }
        // 设置识别的最小人脸比
        engineConfiguration.setDetectFaceScaleVal(Contants.DETECT_FACE_SCALE);
        engineConfiguration.setDetectFaceMaxNum(Contants.DETECT_FACE_MAX_NUM);
        // 功能配置
        initFuncConfiguration(engineConfiguration);
        return engineConfiguration;
    }

    /**
     * 功能配置
     *
     * @param engineConfiguration
     */
    private void initFuncConfiguration(EngineConfiguration engineConfiguration) {
        FunctionConfiguration functionConfiguration = new FunctionConfiguration();
        // 是否支持年龄检测
        functionConfiguration.setSupportAge(Contants.SUPPORT_AGE);
        // 是否支持3d 检测
        functionConfiguration.setSupportFace3dAngle(Contants.SUPPORT_FACE_3D_ANGLE);
        // 是否支持人脸检测
        functionConfiguration.setSupportFaceDetect(Contants.SUPPORT_FACE_DETECT);
        // 是否支持人脸识别
        functionConfiguration.setSupportFaceRecognition(Contants.SUPPORT_FACE_RECOGNITION);
        // 是否支持性别检测
        functionConfiguration.setSupportGender(Contants.SUPPORT_GENDER);
        // 是否支持活体检测
        functionConfiguration.setSupportLiveness(Contants.SUPPORT_LIVENESS);
        // 是否支持IR活体检测
        functionConfiguration.setSupportIRLiveness(Contants.SUPPORT_IR_LIVENESS);
        engineConfiguration.setFunctionConfiguration(functionConfiguration);
    }
}
