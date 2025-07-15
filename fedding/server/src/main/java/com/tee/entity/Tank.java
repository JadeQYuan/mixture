package com.tee.entity;

import lombok.Data;

/**
 * 料罐信息实体类
 * 对应 tank_info 表
 */
@Data
public class Tank {
    private Integer id;            // 料罐ID，主键
    private String tankNo;         // 料罐编号（唯一标识）
    private String remark;         // 备注信息
    private Integer userId;        // 所属用户ID（关联user_info表）
    private String createTime;     // 创建时间
    private String updateTime;     // 更新时间
} 