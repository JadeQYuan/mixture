package cn.domain.entity;

import lombok.Data;

/**
 * 混合料信息实体类
 * 对应mixes_info表，存储加料操作的详细信息
 */
@Data
public class Mixture {
    private Integer id;                        // 加料记录ID，主键
    private Integer tankId;                    // 料罐ID（关联tank_info表）
    private String tankNo;                    // 料罐编号
    private Integer applyUserId;               // 申请用户ID（关联user_info表）
    private String shiftType;                  // 班次类型（day-白班，night-夜班）
    private String materialName;               // 材料名称（10KV、35KV等）
    private String productSpec;                // 产品规格型号
    private Double planWeight;                 // 计划加料重量（kg）
    private Double bottomWeight;               // 罐底重量（kg）
    private Double fullWeight;                 // 满罐重量（kg）
    private Double flameRetardantWeight;       // 阻燃粉重量（kg）
    private Double returnWeight;               // 退料重量（kg）
    private Double actualWeight;               // 实际用料重量（kg）
    private String applyTime;                  // 申请加料时间
    private String feedingTime;                // 实际加料时间
    private Integer feedingUserId;             // 加料操作员ID（关联user_info表）
    private String pickingTime;                // 领料时间
    private Integer pickingUserId;             // 领料操作员ID（关联user_info表）
    private String returnTime;                 // 退料时间
    private Integer returnUserId;              // 退料操作员ID（关联user_info表）
    /**
     * -1. 撤销
     * 0. 申请加料
     * 1. 已加料
     * 2. 已退料
     * 3. 申请备料
     * 4. 已备料
     * 5. 已领料
     */
    private Integer status;                    // 状态
    private String remark;                     // 备注信息
    private Double pickingBottomWeight;        // 领料时底罐重量（kg），阻燃粉称重前
    private Double pickingTotalWeight;         // 领料时总重量（kg），添加阻燃粉后
    private Boolean flameRetardantAbnormal;    // 阻燃粉是否异常（0-正常，1-异常）
    private String createTime;                 // 创建时间
    private String updateTime;                 // 更新时间
} 