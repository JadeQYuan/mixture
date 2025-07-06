package com.tee.emum;

import org.apache.logging.log4j.util.Strings;

public enum RoleEnum {

    OPERATOR("3", "操作员"),
    OPERATOR1("2", "高级操作员"),
    OPERATOR2("1", "物料员");

    private RoleEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getRoleNameByCode(String code) {
        RoleEnum[] values = RoleEnum.values();
        for (RoleEnum value : values) {
            if (value.code.equals(code)) {
                return value.name;
            }
        }
        return Strings.EMPTY;
    }

    private String code;

    private String name;

}
