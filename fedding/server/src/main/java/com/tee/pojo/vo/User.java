package com.tee.pojo.vo;

import lombok.Data;

@Data
public class User {

    private String userId;

    private String userName;

    private String account;

    private String password;

    private String roleCode;

    private String remark;

    private String facePath;

    private String createTime;

    private String updateTime;
}
