package com.app.cinx.api.dto;

import java.util.List;

public class PaginatedApiResponseUserDto {
    private Boolean success;
    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean val) { this.success = val; }

    private String message;
    public String getMessage() { return message; }
    public void setMessage(String val) { this.message = val; }

    private List<UserDto> data;
    public List<UserDto> getData() { return data; }
    public void setData(List<UserDto> val) { this.data = val; }

    private PaginatedMetadata meta;
    public PaginatedMetadata getMeta() { return meta; }
    public void setMeta(PaginatedMetadata val) { this.meta = val; }

}