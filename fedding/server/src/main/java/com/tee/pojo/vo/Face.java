package com.tee.pojo.vo;

import lombok.Data;

@Data
public class Face {
    private String userId;
    private String userName;

    private String facePath;

    private String faceFeature;

    private String createTime;

    private String updateTime;
}
