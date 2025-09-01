package cn.domain.pojo;

import lombok.Data;

@Data
public class MixtureBottomVo {
     private Integer id;
     private Integer returnId;
     private Double returnWeight;
     private Double bottomWeight;
     private Boolean check;
     private String opinion;
}