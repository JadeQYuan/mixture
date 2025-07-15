package com.tee.entity;

import lombok.Data;

/**
 * 用户信息实体类
 * 对应数据库表：user_info
 */
@Data
public class User {
    private Integer id;            // 用户ID，主键
    private String userName;       // 用户姓名
    private String account;        // 登录账号
    private String password;       // 登录密码
    private String roleCode;       // 用户角色代码（admin-管理员，user-普通用户）
    private String facePath;       // 人脸图片路径
    private String faceFeature;    // 人脸特征数据（用于人脸识别）
    private String remark;         // 备注信息
    private String createTime;     // 创建时间
    private String updateTime;     // 更新时间
} 