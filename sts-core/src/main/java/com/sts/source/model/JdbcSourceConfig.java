package com.sts.source.model;

import com.sts.source.StsSource;
import com.sts.source.impl.JdbcSourceImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.sql.DataSource;

/**
 * @author zhq123
 * @date 2025/10/15
 **/
@Getter
@Setter
@AllArgsConstructor
public class JdbcSourceConfig extends BaseSourceConfig{

    private DataSource dataSource;
    private String selectSql;
    private int fetchSize;

    public JdbcSourceConfig(DataSource dataSource, String selectSql) {
        this.dataSource = dataSource;
        this.selectSql = selectSql;
    }

    @Override
    public StsSource buildImpl() {
        return new JdbcSourceImpl(this);
    }
}
