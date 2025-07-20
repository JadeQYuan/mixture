package cn.domain.constant;

public class Contants {

    /**
     * 引擎模式
     */
    public static final String DETECT_MODE = "IMAGE";

    /**
     * 配置人脸角度
     */
    public static final String DETECT_FACE_ORIENT_PRIORITY = "ASF_OP_ALL_OUT";

    /**
     * 设置识别的最小人脸比
     */
    public static final Integer DETECT_FACE_SCALE = 32;

    /**
     * 最大检测人脸数
     */
    public static final Integer DETECT_FACE_MAX_NUM = 8;

    /**
     * 是否支持3d 检测
     */
    public static final Boolean SUPPORT_FACE_3D_ANGLE = true;

    /**
     * 是否支持人脸检测
     */
    public static final Boolean SUPPORT_FACE_DETECT = true;

    /**
     * 是否支持人脸识别
     */
    public static final Boolean SUPPORT_FACE_RECOGNITION = true;

    /**
     * 性别检测
     */
    public static final Boolean SUPPORT_GENDER = true;

    /**
     * 年龄检测
     */
    public static final Boolean SUPPORT_AGE = true;

    /**
     * 是否支持活体检测
     */
    public static final Boolean SUPPORT_LIVENESS = true;

    /**
     * 是否至此IR活体检测
     */
    public static final Boolean SUPPORT_IR_LIVENESS = true;


    public static final String AUTHORIZATION = "Authorization";
}
