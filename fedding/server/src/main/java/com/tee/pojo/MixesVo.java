package com.tee.pojo;

import lombok.Data;

/**
 * 混合料信息实体类
 * 对应mixes_info表，存储加料操作的详细信息
 */
@Data
public class MixesVo {
    private Integer id;
    private Integer tankId;
    private Integer tankNo;
    private Integer applyUserId;
    private String applyUserName;
    private String applyUserAccount;
    private String shiftType;
    private String materialName;
    private String productSpec;
    private Double planWeight;
    private Double bottomWeight;
    private Double fullWeight;
    private Double flameRetardantWeight;
    private Double actualWeight;
    private String applyTime;
    private String feedingTime;
    private Integer feedingUserId;
    private String returnTime;
    private Integer returnUserId;
    private Integer status;
    private String remark;
    private String createTime;
    private String updateTime;
} 