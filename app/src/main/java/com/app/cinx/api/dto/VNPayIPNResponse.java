package com.app.cinx.api.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class VNPayIPNResponse {
    @SerializedName("RspCode")
    private String RspCode;

    public String getRspCode() {
        return RspCode;
    }

    public void setRspCode(String RspCode) {
        this.RspCode = RspCode;
    }

    @SerializedName("Message")
    private String Message;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    // mock field preserved for ui consistency
}
