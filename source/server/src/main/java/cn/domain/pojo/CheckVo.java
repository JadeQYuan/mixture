package cn.domain.pojo;

import lombok.Data;

@Data
public class CheckVo {
     private Integer id;
     private Integer tankId;
     private String tankNo;
     private Integer returnId;
     private String returnTime;
     private Double returnWeight;
     private Integer bottomId;
     private Double bottomWeight;
     private String opinion;
     private Integer userId;
     private String userName;
     private String userAccount;
     private String adminOpinion;
     private Integer adminId;
     private String adminName;
     private String adminAccount;
     private Integer status;
     private Double correctWeight;
     private String createTime;
     private String updateTime;
} 