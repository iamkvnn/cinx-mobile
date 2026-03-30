package com.app.cinx.api.dto;

import java.util.List;

public class VNPayIPNResponse {
    private String RspCode;
    public String getRspCode() { return RspCode; }
    public void setRspCode(String val) { this.RspCode = val; }

    private String Message;
    public String getMessage() { return Message; }
    public void setMessage(String val) { this.Message = val; }

}