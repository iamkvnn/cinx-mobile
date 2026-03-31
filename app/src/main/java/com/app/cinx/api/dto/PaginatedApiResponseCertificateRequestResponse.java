package com.app.cinx.api.dto;

import java.util.List;

public class PaginatedApiResponseCertificateRequestResponse {
    private Boolean success;
    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean val) { this.success = val; }

    private String message;
    public String getMessage() { return message; }
    public void setMessage(String val) { this.message = val; }

    private List<CertificateRequestResponse> data;
    public List<CertificateRequestResponse> getData() { return data; }
    public void setData(List<CertificateRequestResponse> val) { this.data = val; }

    private PaginatedMetadata meta;
    public PaginatedMetadata getMeta() { return meta; }
    public void setMeta(PaginatedMetadata val) { this.meta = val; }

}