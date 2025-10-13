package com.sts.source.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhq123
 * @date 2025/10/13
 **/
@AllArgsConstructor
@Getter
@Setter
public class TextSourceConfig {

    private String filePath;
    private String fieldSeparator = ",";

    public TextSourceConfig(String filePath) {
        this.filePath = filePath;
    }
}
