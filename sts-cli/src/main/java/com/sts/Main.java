package com.sts;

import com.sts.source.StsSource;
import com.sts.source.impl.CsvSourceImpl;
import com.sts.source.impl.ExcelSourceImpl;
import com.sts.source.impl.JdbcSourceImpl;
import com.sts.source.impl.TextSourceImpl;
import com.sts.source.model.CsvSourceConfig;
import com.sts.source.model.ExcelSourceConfig;
import com.sts.source.model.JdbcSourceConfig;
import com.sts.source.model.TextSourceConfig;
import lombok.extern.slf4j.Slf4j;
import org.stone.beecp.BeeDataSource;
import org.stone.beecp.BeeDataSourceConfig;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhq123
 * @date 2025/8/16
 **/
@Slf4j
public class Main {
    public static void main(String[] args) {
        testJDBC();
    }

    public static void testJDBC() {
        BeeDataSourceConfig beeDataSourceConfig = new BeeDataSourceConfig();
        beeDataSourceConfig.setJdbcUrl("jdbc:mysql://192.168.10.110:3306/zhq-gateway");
        beeDataSourceConfig.setUsername("root");
        beeDataSourceConfig.setPassword("root");
        beeDataSourceConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        BeeDataSource beeDataSource = new BeeDataSource(beeDataSourceConfig);
        JdbcSourceConfig jdbcSourceConfig = new JdbcSourceConfig(beeDataSource, "select * from application_interface", Integer.MIN_VALUE);
        try (StsSource stsSource = new JdbcSourceImpl(jdbcSourceConfig)) {
            printHeaderAndData(stsSource);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            beeDataSource.close();
        }

    }

    public static void testText() {
        TextSourceConfig textSourceConfig = new TextSourceConfig("D:\\JavaSpace\\sts\\sts-core\\src\\main\\resources\\test.csv");
        try(StsSource stsSource = new TextSourceImpl(textSourceConfig)){
            printHeaderAndData(stsSource);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void testCsv() {
        CsvSourceConfig csvSourceConfig = new CsvSourceConfig("D:\\JavaSpace\\sts\\sts-core\\src\\main\\resources\\test.csv");
        try(StsSource stsSource = new CsvSourceImpl(csvSourceConfig)){
            printHeaderAndData(stsSource);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void testExcel() {
        ExcelSourceConfig excelSourceConfig = new ExcelSourceConfig("D:\\JavaSpace\\sts\\sts-core\\src\\main\\resources\\test.xlsx");
        try(StsSource stsSource = new ExcelSourceImpl(excelSourceConfig)){
            printHeaderAndData(stsSource);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void printHeaderAndData(StsSource stsSource) {
        List<String> header = stsSource.getHeader();
        log.info("header: {}", header);
        List<List<String>> dataList = stsSource.getDataStream().collect(Collectors.toList());
        dataList.forEach(data -> {
            log.info("data: {}", data);
        });
    }
}
