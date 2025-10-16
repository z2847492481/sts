package com.sts.source.impl;

import com.sts.source.StsSource;
import com.sts.source.model.JdbcSourceConfig;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author zhq123
 * @date 2025/10/15
 **/
@Slf4j
public class JdbcSourceImpl implements StsSource {

    private Stream<List<String>> jdbcReader;
    private JdbcSourceConfig jdbcSourceConfig;

    public JdbcSourceImpl(JdbcSourceConfig jdbcSourceConfig) {
        this.jdbcSourceConfig = jdbcSourceConfig;
    }

    @Override
    public List<String> getHeader() {
        Connection connection = null;
        try {
            connection = jdbcSourceConfig.getDataSource().getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    jdbcSourceConfig.getSelectSql(),
                    // 这两个参数是对ResultSet的设置，默认是ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY)) {
                preparedStatement.setFetchSize(jdbcSourceConfig.getFetchSize());
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    List<String> header = new ArrayList<>(columnCount);
                    for (int i = 1; i <= columnCount; i++) {
                        header.add(metaData.getColumnName(i));
                    }
                    return header;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    connection.commit();
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public Stream<List<String>> getDataStream() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ResultSetIterator resultSetIterator = null;
        try {
            connection = jdbcSourceConfig.getDataSource().getConnection();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(
                    jdbcSourceConfig.getSelectSql(),
                    // 这两个参数是对ResultSet的设置，默认是ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setFetchSize(jdbcSourceConfig.getFetchSize());
            resultSet = preparedStatement.executeQuery();
            int columnCount = resultSet.getMetaData().getColumnCount();
            resultSetIterator = new ResultSetIterator(resultSet, columnCount);
        } catch (Exception e) {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
            throw new RuntimeException(e.getMessage(), e);
        }
        jdbcReader = StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(resultSetIterator, Spliterator.ORDERED), false)
                .onClose(asUncheckedRunnable(resultSet))
                .onClose(asUncheckedRunnable(preparedStatement))
                .onClose(asUncheckedRunnable(connection));
        return jdbcReader;
    }

    @Override
    public void close() throws Exception {
        if (jdbcReader != null) {
            jdbcReader.close();
            log.info("jdbcReader closed");
        }
    }

    private Runnable asUncheckedRunnable(AutoCloseable c) {
        return () -> {
            try {
                if (c instanceof ResultSet) {
                    log.info("ResultSet closed");
                } else if (c instanceof PreparedStatement) {
                    log.info("PreparedStatement closed");
                } else if (c instanceof Connection) {
                    log.info("Connection commit");
                    ((Connection) c).commit();
                    log.info("Connection closed");
                }
                c.close();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        };
    }

    /**
     * 解析每一行记录，next返回的结果是这一行每一列的值
     */
    static class ResultSetIterator implements Iterator<List<String>> {

        private final ResultSet resultSet;
        private int headSize;

        public ResultSetIterator(ResultSet resultSet, int headSize) {
            this.resultSet = resultSet;
            this.headSize = headSize;
        }

        @Override
        public boolean hasNext() {
            try {
                return resultSet.next();
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        @Override
        public List<String> next() {
            List<String> dataRowList = new ArrayList<>(headSize);
            try {
                for (int i = 1; i <= headSize; i++) {
                    Object object = resultSet.getObject(i);
                    if (object == null) {
                        dataRowList.add(null);
                        continue;
                    }
                    // 特殊处理 不丢失数据精度
                    if (object instanceof BigDecimal) {
                        dataRowList.add((((BigDecimal) object).toPlainString()));
                        continue;
                    }
                    dataRowList.add(String.valueOf(object));
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            return dataRowList;
        }
    }

}
