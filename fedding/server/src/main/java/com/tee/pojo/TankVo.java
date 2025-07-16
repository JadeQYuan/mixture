package com.tee.pojo;

import lombok.Data;

/**
 * 料罐信息VO，包含用户信息
 */
@Data
public class TankVo {
    private Integer id;            // 料罐ID
    private String tankNo;         // 料罐编号
    private String remark;         // 备注
    private Integer userId;        // 用户ID
    private String userName;       // 用户姓名
    private String userAccount;        // 用户账号
    private String updateTime;     // 更新时间
} 