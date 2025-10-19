package com.sts.source.model;

import com.sts.source.StsSource;
import com.sts.source.impl.CsvSourceImpl;
import lombok.*;

/**
 * @author zhq123
 * @date 2025/8/16
 **/

@Getter
@AllArgsConstructor
public class CsvSourceConfig extends BaseSourceConfig{

    /**
     * 是否跳过空行
     */
    private boolean skipEmptyRows = true;

    /**
     * 列分隔符
     */
    private Character fieldSeparator = ',';

    /**
     * a, b, c
     * 1, 2, "3,4"
     * 防止上述情况，使用引号来确定一列特殊内容的起止
     */
    private Character quoteCharacter = '"';

    /**
     * 文件路径
     */
    private String filePath;

    public CsvSourceConfig(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public StsSource buildImpl() {
        return new CsvSourceImpl(this);
    }
}
