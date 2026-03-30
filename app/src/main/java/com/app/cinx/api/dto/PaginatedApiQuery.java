package com.app.cinx.api.dto;

import java.util.List;

public class PaginatedApiQuery {
    private Integer page;
    public Integer getPage() { return page; }
    public void setPage(Integer val) { this.page = val; }

    private Integer size;
    public Integer getSize() { return size; }
    public void setSize(Integer val) { this.size = val; }

    private String query;
    public String getQuery() { return query; }
    public void setQuery(String val) { this.query = val; }

    private String sort;
    public String getSort() { return sort; }
    public void setSort(String val) { this.sort = val; }

}