package com.sts.source.model;

import com.sts.source.StsSource;
import com.sts.source.impl.TextSourceImpl;
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
public class TextSourceConfig extends BaseSourceConfig{

    private String filePath;
    private String fieldSeparator = ",";

    public TextSourceConfig(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public StsSource buildImpl() {
        return new TextSourceImpl(this);
    }
}
