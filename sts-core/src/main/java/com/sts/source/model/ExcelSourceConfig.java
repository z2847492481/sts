package com.sts.source.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhq123
 * @date 2025/8/16
 **/

@Getter
public class ExcelSourceConfig {

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 读哪个sheet
     */
    private String sheetName;

    public ExcelSourceConfig(String filePath) {
        this.filePath = filePath;
    }

    public ExcelSourceConfig(String filePath, String sheetName) {
        this.filePath = filePath;
        this.sheetName = sheetName;
    }
}
