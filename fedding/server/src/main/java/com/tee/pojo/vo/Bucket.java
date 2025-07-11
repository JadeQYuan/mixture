package com.tee.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Bucket {

    private String id;

    private String userId;

    private String userName;

    private String bucketNo;

    private BigDecimal capacity; // 容量

    private String spec;

    private BigDecimal capacityDdd; // 加料

    private BigDecimal abs; // 阻燃粉

    private String type; // add 加料，del 退料

    private String status; // 状态 1 已处理，0未处理

    private String remark;

    private String createTime;

    private String updateTime;
}
