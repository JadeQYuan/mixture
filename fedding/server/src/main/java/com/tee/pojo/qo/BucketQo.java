package com.tee.pojo.qo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BucketQo {

    private String id;

    private String userId;

    private String userName;

    private String bucketNo;

    private BigDecimal capacity; // 罐底，容量

    private String spec;

    private BigDecimal capacityAdd; // 增加容量

    private BigDecimal abs; // 阻燃粉

    private String type; // add 加料，del 退料

    private String status; // 0 未处理，1已处理

    private String startTime;

    private String endTime;

    private String remark;
}
