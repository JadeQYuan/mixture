package cn.domain.pojo;

import lombok.Data;

/**
 * 用户查询参数对象
 */
@Data
public class UserQo extends PageQo {
    /** 用户名 */
    private String userName;
    /** 账号 */
    private String account;
} 