package com.longtao.service.impl;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.longtao.config.DynamicDataSource;
import com.longtao.entity.constant.MigrationEnum;
import com.longtao.mapper.DataTableMapper;
import com.longtao.service.MigrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MigrationServiceImpl implements MigrationService {

    @Autowired
    private DataTableMapper dataTableMapper;

    @Autowired
    private DynamicDataSource dynamicDataSource;

    private static final Set<String> ignoreTableSet = Sets.newHashSet("migration_log", "user", "user_role", "role"
            , "user_auth_token", "user_dashboard_views", "user_auth", "test_data", "temp_user"
            , "team_role", "team_member", "team_group", "team", "org", "org_user", "server_lock");

    private static final String DATA_TYPE = "data_type";
    private static final String COLUMN_NAME = "column_name";


    @Override
    public void migrationToPostgres() {
        StopWatch watch = new StopWatch();
        // 获取所有表名
        List<String> tableList = dataTableMapper.selectAllTable();
        for (String tableName : tableList) {
            if (ignoreTableSet.contains(tableName)) {
                continue;
            }
            watch.start(StrUtil.format("{}表迁移", tableName));
            List<Map<String, Object>> list = dataTableMapper.selectListByTableName(tableName);
            log.info("查询:{}表,数据大小:{},正在迁移到postgres", tableName, list.size());
            dynamicDataSource.setDataSourceKey("postgresDataSource");
            log.info("获取表结构");
            List<Map<String, Object>> tableColumns = dataTableMapper.getTableColumns(tableName);
            // 将tableColumns转成一个map,key:字段名称,value:字段类型
            List<String> booleanColumnList = tableColumns.stream().filter(f -> Objects.equals(f.get(DATA_TYPE), "boolean"))
                    .map(m -> m.get(COLUMN_NAME).toString())
                    .collect(Collectors.toList());
            List<String> timestampColumnList = tableColumns.stream().filter(f -> Objects.equals(f.get(DATA_TYPE), "timestamp without time zone"))
                    .map(m -> m.get(COLUMN_NAME).toString())
                    .collect(Collectors.toList());
            log.info("清空表数据中");
            dataTableMapper.truncateByTableName(tableName);
            log.info("迁移中!");
            for (Map<String, Object> map : list) {
                Map<String, Object> dataMap = map.entrySet().stream().peek(m -> {
                            if (booleanColumnList.contains(m.getKey())) {
                                m.setValue(MigrationEnum.BOOLEAN_TYPE.convertBoolean(m.getValue().toString()));
                            }
                            if (timestampColumnList.contains(m.getKey())) {
                                m.setValue(new Timestamp(new Date().getTime()));
                            }
                        })
                        .collect(Collectors.toMap(e -> "\"" + e.getKey() + "\"", Map.Entry::getValue));
                dataTableMapper.migration(Lists.newArrayList(dataMap.keySet()), Lists.newArrayList(dataMap.values()), tableName);
            }
            watch.stop();
            dynamicDataSource.restoreDefaultDataSourceKey();
        }
        log.info("迁移完成,耗时为如下:");
        log.info(watch.prettyPrint());
    }
}
