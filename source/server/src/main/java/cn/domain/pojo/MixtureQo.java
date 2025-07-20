package cn.domain.pojo;

import java.util.List;

import lombok.Data;

/**
 * 加料管理查询参数对象
 */
@Data
public class MixtureQo extends PageQo {
    /** 人员关键字 */
    private String applyUserKey;
    /** 料罐编号 */
    private String tankNo;
    /** 班次 */
    private String shiftType;
    /** 材料名称 */
    private String materialName;

    private List<Integer> status;

    // 时间范围查询字段
    private String applyStartTime;
    private String applyEndTime;
    private String feedingStartTime;
    private String feedingEndTime;
    private String returnStartTime;
    private String returnEndTime;
} 