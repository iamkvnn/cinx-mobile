package com.app.cinx.api.dto;

public class PageMeta {
    private int page;
    private int limit;
    private int totalElements;
    private int totalPages;

    public int getPage()          { return page; }
    public int getLimit()         { return limit; }
    public int getTotalElements() { return totalElements; }
    public int getTotalPages()    { return totalPages; }
}
