package cn.domain.pojo;

import lombok.Data;

/**
 * 加料管理查询参数对象
 */
@Data
public class CheckQo extends PageQo {

    private String tankNo;

    private Integer status;
} 