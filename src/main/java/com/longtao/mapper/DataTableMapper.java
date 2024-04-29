package com.longtao.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DataTableMapper {

    List<String> selectAllTable();

    List<Map<String, Object>> selectListByTableName(@Param("tableName") String tableName);


    List<Map<String,Object>> getTableColumns(@Param("tableName") String tableName);

    void truncateByTableName(@Param("tableName") String tableName);
    int migration(@Param("columnList") List<String> columnList, @Param("values") List<Object> values, @Param("tableName") String tableName);
}
