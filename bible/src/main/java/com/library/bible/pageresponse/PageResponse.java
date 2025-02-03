package com.library.bible.pageresponse;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private int totalPages;
    private long totalElements;
    private boolean last; // 마지막 페이지(true : 마지막 페이지)
}