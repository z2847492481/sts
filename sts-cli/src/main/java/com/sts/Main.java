package com.sts;

import com.sts.source.StsSource;
import com.sts.source.impl.CsvSourceImpl;
import com.sts.source.impl.ExcelSourceImpl;
import com.sts.source.model.CsvSourceConfig;
import com.sts.source.model.ExcelSourceConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhq123
 * @date 2025/8/16
 **/
@Slf4j
public class Main {
    public static void main(String[] args) {
        testExcel();
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
