package com.sts.source;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author zhq123
 * @date 2025/8/16
 **/
public interface StsSource extends AutoCloseable{

    List<String> getHeader();

    Stream<List<String>> getDataStream();
}
