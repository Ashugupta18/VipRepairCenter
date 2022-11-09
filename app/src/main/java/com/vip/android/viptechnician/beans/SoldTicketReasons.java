package com.vip.android.viptechnician.beans;

import java.util.List;

public class SoldTicketReasons
{
    private Integer statuscode;

    private String statusmessage;

    private List<ReasonList> reasonList = null;

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


    public List<ReasonList> getReasonList() {
        return reasonList;
    }

    public void setReasonList(List<ReasonList> reasonList) {
        this.reasonList = reasonList;
    }

}
