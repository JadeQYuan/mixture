package com.tee.pojo;

import lombok.Data;

/**
 * 加料管理查询参数对象
 */
@Data
public class MixesQo extends PageQo {
    /** 人员关键字 */
    private String applyUserKey;
    /** 料罐编号 */
    private String tankNo;
    /** 班次 */
    private String shiftType;
    /** 材料名称 */
    private String materialName;

    private Integer status;
} 