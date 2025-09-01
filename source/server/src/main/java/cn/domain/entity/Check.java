package cn.domain.entity;

import lombok.Data;

/**
 * 校验信息实体类
 * 对应 tank_info 表
 */
@Data
public class Check {
    private Integer id;                          // 加料记录ID，主键，自增
    private Integer tankId;                      // 料罐ID（关联tank_info表）
    private String tankNo;                       // 料罐编号
    private Integer returnId;                    // 退料记录
    private Double returnWeight;                 // 退罐重量（kg）
    private Integer bottomId;                    // 加料记录
    private Double bottomWeight;                 // 罐底重量（kg）
    private String opinion;                      // 物料员意见
    private Integer userId;                      // 物料员ID
    private String adminOpinion;                 // 管理员意见
    private Integer adminId;                     // 管理员ID
    /**
     * 0. 未处理
     * 1. 已处理-修改
     * 2. 已处理-不修改
     */
    private Integer status;                      // 状态
    private Double correctWeight;                // 校正重量（kg）
    private String createTime;                   // 创建时间
    private String updateTime;                   // 更新时间
}

