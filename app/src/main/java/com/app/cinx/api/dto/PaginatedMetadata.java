package com.app.cinx.api.dto;

import java.util.List;

public class PaginatedMetadata {
    private Integer page;
    public Integer getPage() { return page; }
    public void setPage(Integer val) { this.page = val; }

    private Integer limit;
    public Integer getLimit() { return limit; }
    public void setLimit(Integer val) { this.limit = val; }

    private Long totalElements;
    public Long getTotalElements() { return totalElements; }
    public void setTotalElements(Long val) { this.totalElements = val; }

    private Integer totalPages;
    public Integer getTotalPages() { return totalPages; }
    public void setTotalPages(Integer val) { this.totalPages = val; }

}