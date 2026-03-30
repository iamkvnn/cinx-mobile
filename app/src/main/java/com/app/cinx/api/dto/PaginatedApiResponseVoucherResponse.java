package com.app.cinx.api.dto;

import java.util.List;

public class PaginatedApiResponseVoucherResponse {
    private Boolean success;
    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean val) { this.success = val; }

    private String message;
    public String getMessage() { return message; }
    public void setMessage(String val) { this.message = val; }

    private List<VoucherResponse> data;
    public List<VoucherResponse> getData() { return data; }
    public void setData(List<VoucherResponse> val) { this.data = val; }

    private PaginatedMetadata meta;
    public PaginatedMetadata getMeta() { return meta; }
    public void setMeta(PaginatedMetadata val) { this.meta = val; }

}