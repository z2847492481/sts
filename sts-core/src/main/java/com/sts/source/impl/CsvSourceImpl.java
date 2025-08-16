package com.sts.source.impl;

import com.sts.source.StsSource;
import com.sts.source.model.CsvSourceConfig;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author zhq123
 * @date 2025/8/16
 **/
@Slf4j
public class CsvSourceImpl implements StsSource {

    private CsvReader csvReader;

    private CsvSourceConfig csvSourceConfig;

    public CsvSourceImpl(CsvSourceConfig csvSourceConfig) {
        this.csvSourceConfig = csvSourceConfig;
    }

    private CsvReader buildCsvReader(){
        try {
            return CsvReader
                    .builder()
                    .skipEmptyRows(csvSourceConfig.isSkipEmptyRows())
                    .fieldSeparator(csvSourceConfig.getFieldSeparator())
                    .quoteCharacter(csvSourceConfig.getQuoteCharacter())
                    .build(Paths.get(csvSourceConfig.getFilePath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getHeader() {
        try(CsvReader reader = buildCsvReader()){
            return reader
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("csv文件为空"))
                    .getFields();
        }catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public Stream<List<String>> getDataStream() {
        csvReader = buildCsvReader();
        return csvReader.stream()
                .skip(1)
                .map(CsvRow::getFields);
    }

    @Override
    public void close() throws Exception {
        if (csvReader != null) {
            csvReader.close();
            log.info("csvReader closed");
        }
    }
}
