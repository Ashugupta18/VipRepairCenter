package com.vip.android.viptechnician.beans;


import java.util.List;

public class SparePartsResponse
{
    private Integer statuscode;
    private String statusmessage;
    private List<SpareParts> spareList = null;

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


    public List<SpareParts> getSpareList() {
        return spareList;
    }

    public void setSpareList(List<SpareParts> spareList) {
        this.spareList = spareList;
    }

}
