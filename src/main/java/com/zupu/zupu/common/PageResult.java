package com.zupu.zupu.common;

import lombok.Getter;

import java.util.List;

/**
 * 分页返回结构。
 */
@Getter
public class PageResult<T> {
    private final List<T> list;
    private final long total;
    private final int pageNum;
    private final int pageSize;

    public PageResult(List<T> list, long total, int pageNum, int pageSize) {
        this.list = list;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
}
