package com.app.cinx.api.dto;

import java.util.List;

/** Generic paginated API wrapper: { success, message, data[], meta } */
public class ApiListResponse<T> {
    private boolean    success;
    private String     message;
    private List<T>    data;
    private PageMeta   meta;

    public boolean   isSuccess() { return success; }
    public String    getMessage() { return message; }
    public List<T>   getData()    { return data; }
    public PageMeta  getMeta()    { return meta; }
}
