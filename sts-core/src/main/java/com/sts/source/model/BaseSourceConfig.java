package com.sts.source.model;

import cn.hutool.core.collection.CollectionUtil;
import com.sts.source.StsSource;
import lombok.Getter;

import java.util.List;

/**
 * @author zhq123
 * @date 2025/10/19
 **/
@Getter
public abstract class BaseSourceConfig {

    private boolean hasHeader = true;

    private List<String> headerList;

    public BaseSourceConfig() {
    }

    public BaseSourceConfig(boolean hasHeader, List<String> headerList) {
        if (!hasHeader && CollectionUtil.isEmpty(headerList)) {
            throw new RuntimeException("headerList must not be empty when hasHeader is false");
        }
        this.hasHeader = hasHeader;
        this.headerList = headerList;
    }

    public abstract StsSource buildImpl();
}
