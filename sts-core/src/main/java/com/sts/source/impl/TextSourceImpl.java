package com.sts.source.impl;

import cn.hutool.core.util.StrUtil;
import com.sts.source.StsSource;
import com.sts.source.model.TextSourceConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author zhq123
 * @date 2025/10/13
 **/
@Slf4j
public class TextSourceImpl implements StsSource {

    private Stream<String> textReader;

    private TextSourceConfig textSourceConfig;

    public TextSourceImpl(TextSourceConfig textSourceConfig) {
        this.textSourceConfig = textSourceConfig;
    }

    @Override
    public List<String> getHeader() {

        if (!textSourceConfig.isHasHeader()) {
            return textSourceConfig.getHeaderList();
        }

        try(Stream<String> reader = buildTextReader()) {
            String firstLine = reader.findFirst().orElseThrow(() -> new RuntimeException("文件为空"));
            return StrUtil.split(firstLine, textSourceConfig.getFieldSeparator());
        }
    }

    @Override
    public Stream<List<String>> getDataStream() {
        textReader = buildTextReader();
        if (textSourceConfig.isHasHeader()) {
            textReader = textReader.skip(1);
        }
        return textReader.map(line -> StrUtil.split(line, textSourceConfig.getFieldSeparator()));
    }

    @Override
    public void close() throws Exception {
        if (textReader != null) {
            textReader.close();
            log.info("textReader closed");
        }
    }

    public Stream<String> buildTextReader() {
        try{
            // 这是一个惰性加载的流，不会导致OOM
            return Files.lines(Paths.get(textSourceConfig.getFilePath()));
        }catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
