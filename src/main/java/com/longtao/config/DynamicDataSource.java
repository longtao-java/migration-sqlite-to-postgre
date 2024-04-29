package com.longtao.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {

    private String dataSourceKey = "sqliteDataSource";


    /**
     * 默认sqlite数据源
     *
     * @return
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return this.dataSourceKey;
    }



    public void setDataSourceKey(String dataSourceKey) {
        this.dataSourceKey = dataSourceKey;
    }

    public void restoreDefaultDataSourceKey() {
        this.dataSourceKey = "sqliteDataSource";
    }

}
