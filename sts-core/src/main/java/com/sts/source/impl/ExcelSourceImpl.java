package com.sts.source.impl;

import cn.hutool.core.util.StrUtil;
import com.sts.source.StsSource;
import com.sts.source.model.ExcelSourceConfig;
import lombok.extern.slf4j.Slf4j;
import org.ttzero.excel.reader.ExcelReader;
import org.ttzero.excel.reader.Row;
import org.ttzero.excel.reader.Sheet;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhq123
 * @date 2025/8/17
 **/
@Slf4j
public class ExcelSourceImpl implements StsSource {


    private ExcelReader excelReader;

    private ExcelSourceConfig excelSourceConfig;

    public ExcelSourceImpl(ExcelSourceConfig excelSourceConfig) {
        this.excelSourceConfig = excelSourceConfig;
    }

    @Override
    public List<String> getHeader() {
        if (!excelSourceConfig.isHasHeader()) {
            return excelSourceConfig.getHeaderList();
        }
        try(ExcelReader reader = buildExcelReader()) {
            return getSheet(reader)
                    .getHeader()
                    .toMap().values()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(String::valueOf)
                    .collect(Collectors.toList());
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Sheet getSheet(ExcelReader excelReader) {
        if (StrUtil.isBlank(excelSourceConfig.getSheetName())) {
            return excelReader.sheet(0);
        } else {
            return excelReader.sheet(excelSourceConfig.getSheetName());
        }
    }

    @Override
    public Stream<List<String>> getDataStream() {
        ExcelReader reader = buildExcelReader();
        Stream<Row> stream = getSheet(reader)
                .dataRows();
        if (excelSourceConfig.isHasHeader()) {
            stream = stream.skip(1);
        }
        return stream
                .filter(Objects::nonNull)
                .map(row -> row.toMap().values().stream().map(this::getCellValue).collect(Collectors.toList()));
    }

    public String getCellValue(Object o) {
        return Objects.nonNull(o) && StrUtil.isNotBlank(o.toString()) ? o.toString() : null;
    }

    @Override
    public void close() throws Exception {
        if (excelReader != null) {
            excelReader.close();
        }
    }

    private ExcelReader buildExcelReader() {
        try{
           return  ExcelReader.read(Paths.get(excelSourceConfig.getFilePath()));
        }catch (Exception e) {
            log.info("构建ExcelReader失败");
            throw new RuntimeException(e);
        }
    }
}
