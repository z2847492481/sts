package com.sts;

import com.sts.source.StsSource;
import com.sts.source.impl.CsvSourceImpl;
import com.sts.source.model.CsvSourceConfig;
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
        CsvSourceConfig csvSourceConfig = new CsvSourceConfig("D:\\JavaSpace\\sts\\sts-core\\src\\main\\resources\\test.csv");
        try(StsSource stsSource = new CsvSourceImpl(csvSourceConfig)){
            List<String> header = stsSource.getHeader();
            log.info("header: {}", header);
            List<List<String>> dataList = stsSource.getDataStream().collect(Collectors.toList());
            dataList.forEach(data -> {
                log.info("data: {}", data);
            });
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
