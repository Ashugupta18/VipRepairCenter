package com.vip.android.viptechnician.beans;

import java.io.Serializable;

/**
 * Created by abhiraj on 12/23/2017.
 */

public class TicketBean implements Serializable {

    String name;
    String phone;
    String address;
    String ticketNo;
    String barcode;
    String defect;
    String defectImage;
    String productName;
    String ModelName;
    String ticketStatus;
    String pickupotp;
    String allowwork;
    String prod_id;
    String verified_barcode;
    String repair_replacement;
    String warranty_type;
    String invoice_date;
    String created_date;
    String sl_category;

    String spareRequest;
    String replacementRequest;
    String warrantyImage;
    String billImage;
    String handlingDamageRequest;
    //26Mar 2021 Abhijeet thakur
    String Category_Type;

    public String getCategory_Type() {
        return Category_Type;
    }

    public void setCategory_Type(String category_Type) {
        Category_Type = category_Type;
    }

    public String getVerified_barcode() {
        return verified_barcode;
    }

    public void setVerified_barcode(String verified_barcode) {
        this.verified_barcode = verified_barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getDefect() {
        return defect;
    }

    public void setDefect(String defect) {
        this.defect = defect;
    }

    public String getDefectImage() {
        return defectImage;
    }

    public void setDefectImage(String defectImage) {
        this.defectImage = defectImage;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getModelName() {
        return ModelName;
    }

    public void setModelName(String modelName) {
        ModelName = modelName;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public String getPickupotp() {
        return pickupotp;
    }

    public void setPickupotp(String pickupotp) {
        this.pickupotp = pickupotp;
    }

    public String getAllowwork() {
        return allowwork;
    }

    public void setAllowwork(String allowwork) {
        this.allowwork = allowwork;
    }


    public String getRepair_replacement() {
        return repair_replacement;
    }

    public void setRepair_replacement(String repair_replacement) {
        this.repair_replacement = repair_replacement;
    }

    public String getProd_id() {
        return prod_id;
    }

    public void setProd_id(String prod_id) {
        this.prod_id = prod_id;
    }

    public String getWarranty_type() {
        return warranty_type;
    }

    public void setWarranty_type(String warranty_type) {
        this.warranty_type = warranty_type;
    }

    public String getInvoice_date() {
        return invoice_date;
    }

    public void setInvoice_date(String invoice_date) {
        this.invoice_date = invoice_date;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getSl_category() {
        return sl_category;
    }

    public void setSl_category(String sl_category) {
        this.sl_category = sl_category;
    }

    public String getSpareRequest() {
        return spareRequest;
    }

    public void setSpareRequest(String spareRequest) {
        this.spareRequest = spareRequest;
    }

    public String getReplacementRequest() {
        return replacementRequest;
    }

    public void setReplacementRequest(String replacementRequest) {
        this.replacementRequest = replacementRequest;
    }

    public String getWarrantyImage() {
        return warrantyImage;
    }

    public void setWarrantyImage(String warrantyImage) {
        this.warrantyImage = warrantyImage;
    }

    public String getBillImage() {
        return billImage;
    }

    public void setBillImage(String billImage) {
        this.billImage = billImage;
    }

    public String getHandlingDamageRequest() {
        return handlingDamageRequest;
    }

    public void setHandlingDamageRequest(String handlingDamageRequest) {
        this.handlingDamageRequest = handlingDamageRequest;
    }
}
