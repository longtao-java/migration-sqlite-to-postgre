package com.longtao.entity.constant;

import cn.hutool.core.util.StrUtil;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;

public enum MigrationEnum {


    BOOLEAN_TYPE("boolean");

    String fieldType;

    MigrationEnum(String fieldType) {
        this.fieldType = fieldType;
    }

    <T, R> R convertValue(T value, Function<T, R> function) {
        return function.apply(value);
    }

    /**
     * 转换boolean
     *
     * @param value
     * @return
     */
    public Boolean convertBoolean(String value) {
        return convertValue(value, s -> Objects.equals(s, "1") || Objects.equals(s, "true") || Objects.equals(s, "t"));
    }

    /**
     * 转换timestamp
     *
     * @param value
     * @return
     */
    public Timestamp convertTimestamp(String value) {
        return convertValue(value, s -> new Timestamp(new Date().getTime()));
    }

    public static MigrationEnum getMigrationEnum(String fieldType) {
        for (MigrationEnum migrationEnum : MigrationEnum.values()) {
            if (migrationEnum.fieldType.equals(fieldType)) {
                return migrationEnum;
            }
        }
        throw new RuntimeException(StrUtil.format("[{}]所属类型不存在!", fieldType));
    }
}
