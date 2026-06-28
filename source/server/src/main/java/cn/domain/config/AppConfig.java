package cn.domain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {

    private Double feedThreshold = 5.0;

    private Double bottomThreshold = 5.0;

    /** 领料底罐称重阈值（满罐与底罐重量差超出此值需确认） */
    private Double pickingBottomThreshold = 5.0;

    /** 阻燃粉比例（分母），实际加料重量/阻燃粉重量 的期望比值，默认30 */
    private Double flameRetardantRatio = 30.0;

    /** 阻燃粉比例区间下限（分母），实际加料重量/阻燃粉重量 >= 此值 */
    private Double flameRetardantRatioMin = 25.0;

    /** 阻燃粉比例区间上限（分母），实际加料重量/阻燃粉重量 <= 此值 */
    private Double flameRetardantRatioMax = 35.0;

    /** 加料提醒时间阈值（分钟），申请加料后超过此时间未操作则首页醒目提醒 */
    private Integer pickingTimeThreshold = 30;
}
