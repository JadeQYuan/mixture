package com.tee.pojo.qo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserQo {
    private String userId;
    @NotNull
    private String userName;
    @NotNull
    private String account;

    private String password;

    @NotNull
    private String roleCode;

    private String remark;
}
