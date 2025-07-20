package cn.domain.pojo;

import lombok.Data;

/**
 * 混合料信息实体类
 * 对应mixes_info表，存储加料操作的详细信息
 */
@Data
public class MixtureVo {
    private Integer id;
    private Integer tankId;
    private String tankNo;
    private String shiftType;
    private String materialName;
    private String productSpec;
    private Double planWeight;
    private String applyTime;
    private Integer applyUserId;
    private String applyUserName;
    private String applyUserAccount;
    private Double bottomWeight;
    private Double fullWeight;
    private Double flameRetardantWeight;
    private String feedingTime;
    private Integer feedingUserId;
    private String feedingUserName;
    private String feedingUserAccount;
    private String returnTime;
    private Double returnWeight;
    private Double actualWeight;
    private Integer status;
    private String remark;
    private String createTime;
    private String updateTime;
} 