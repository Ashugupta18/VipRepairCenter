package com.vip.android.viptechnician.beans;

import com.google.gson.annotations.SerializedName;

public class UpdateTokenResponse
{
    @SerializedName("statuscode")
    private Integer statuscode;

    @SerializedName("statusmessage")
    private String statusmessage;


    public Integer getStatuscode() {
        return statuscode;
    }

    public void setStatuscode(Integer statuscode) {
        this.statuscode = statuscode;
    }

    public String getStatusmessage() {
        return statusmessage;
    }

    public void setStatusmessage(String statusmessage) {
        this.statusmessage = statusmessage;
    }


}
