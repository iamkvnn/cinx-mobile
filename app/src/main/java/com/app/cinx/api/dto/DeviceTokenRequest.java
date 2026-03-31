package com.app.cinx.api.dto;

import java.util.List;

public class DeviceTokenRequest {
    private String fcmToken;
    public String getFcmToken() { return fcmToken; }
    public void setFcmToken(String val) { this.fcmToken = val; }

    private String deviceInfo;
    public String getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(String val) { this.deviceInfo = val; }

}