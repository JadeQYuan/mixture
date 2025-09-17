package cn.domain.config;

import cn.domain.exception.AppException;
import com.arcsoft.face.ActiveFileInfo;
import com.arcsoft.face.EngineConfiguration;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FunctionConfiguration;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectOrient;
import com.arcsoft.face.enums.ErrorInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "face")
public class ArcSoftConfig {

    /**
     * 引擎模式
     */
    private String detectMode = "IMAGE";

    /**
     * 配置人脸角度
     */
    private String detectFaceOrientPriority = "ASF_OP_ALL_OUT";

    /**
     * 设置识别的最小人脸比
     */
    private Integer detectFaceScale = 32;

    /**
     * 最大检测人脸数
     */
    private Integer detectFaceMaxNum = 8;

    /**
     * 是否支持3d 检测
     */
    private Boolean supportFace3dAngle = true;

    /**
     * 是否支持人脸检测
     */
    private Boolean supportFaceDetect = true;

    /**
     * 是否支持人脸识别
     */
    private Boolean supportFaceRecognition = true;

    /**
     * 性别检测
     */
    private Boolean supportGender = true;

    /**
     * 年龄检测
     */
    private Boolean supportAge = true;

    /**
     * 是否支持活体检测
     */
    private Boolean supportLiveness = true;

    /**
     * 是否至此IR活体检测
     */
    private Boolean supportIrLiveness = true;

    private String appId;

    // win平台sdk
    private String sdkKey;

    private String activeKey;

    // dll/so库路径
    private String libPath;

    /**
     * 装载FaceEngine交给spring托管
     *
     * @return
     */
    @Bean
    public FaceEngine faceEngine() {
        libPath = new File("").getAbsolutePath() + libPath;
        FaceEngine faceEngine = new FaceEngine(libPath);
        int errorCode = faceEngine.activeOnline(appId, sdkKey, activeKey);
        if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
            log.error("引擎注册失败");
            throw new AppException("引擎注册失败");
        }
        ActiveFileInfo activeFileInfo = new ActiveFileInfo();
        faceEngine.getActiveFileInfo(activeFileInfo);
        log.info("activeInfo, {}", activeFileInfo);
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
        if ("IMAGE".equals(detectMode)) {
            engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);
        } else {
            engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_VIDEO);
        }
        // 配置人脸角度 全角度 ASF_OP_ALL_OUT 不够准确且检测速度慢
        switch (detectFaceOrientPriority) {
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
//        engineConfiguration.setDetectFaceScaleVal(detectFaceScale);
        engineConfiguration.setDetectFaceMaxNum(detectFaceMaxNum);
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
        functionConfiguration.setSupportAge(supportAge);
        // 是否支持3d 检测
//        functionConfiguration.setSupportFace3dAngle(supportFace3dAngle);
        // 是否支持人脸检测
        functionConfiguration.setSupportFaceDetect(supportFaceDetect);
        // 是否支持人脸识别
        functionConfiguration.setSupportFaceRecognition(supportFaceRecognition);
        // 是否支持性别检测
        functionConfiguration.setSupportGender(supportGender);
        // 是否支持活体检测
        functionConfiguration.setSupportLiveness(supportLiveness);
        // 是否支持IR活体检测
        functionConfiguration.setSupportIRLiveness(supportIrLiveness);
        engineConfiguration.setFunctionConfiguration(functionConfiguration);
    }
}
