package com.vip.android.viptechnician.beans;

/**
 * Created by Android on 12/21/2017.
 */

public class CPBean
{
    private String cp_id;
    private String cp_name;
    private String cp_phone;
    private String cp_addr;
    private String date;
    private String sap_code;
    private String ticketCountOpen;

    public String getCp_id() {
        return cp_id;
    }

    public void setCp_id(String cp_id) {
        this.cp_id = cp_id;
    }

    public String getCp_name() {
        return cp_name;
    }

    public void setCp_name(String cp_name) {
        this.cp_name = cp_name;
    }

    public String getCp_phone() {
        return cp_phone;
    }

    public void setCp_phone(String cp_phone) {
        this.cp_phone = cp_phone;
    }

    public String getCp_addr() {
        return cp_addr;
    }

    public void setCp_addr(String cp_addr) {
        this.cp_addr = cp_addr;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTicketCountOpen() {
        return ticketCountOpen;
    }

    public void setTicketCountOpen(String ticketCountOpen) {
        this.ticketCountOpen = ticketCountOpen;
    }


    public String getSap_code() {
        return sap_code;
    }

    public void setSap_code(String sap_code) {
        this.sap_code = sap_code;
    }

}
