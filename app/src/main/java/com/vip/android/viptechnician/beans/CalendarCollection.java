package com.vip.android.viptechnician.beans;

import java.util.ArrayList;

/**
 * Created by abhiraj on 11/17/2017.
 */

public class CalendarCollection {

    public String date="",id,name,contactno,pincode,city,address,state,country;
    public String event_message="";

    public static ArrayList<CalendarCollection> date_collection_arr;
    /*public CalendarCollection(String date, String event_message){

        this.date=date;
        this.event_message=event_message;

    }*/

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEvent_message() {
        return event_message;
    }

    public void setEvent_message(String event_message) {
        this.event_message = event_message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactno() {
        return contactno;
    }

    public void setContactno(String contactno) {
        this.contactno = contactno;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public static ArrayList<CalendarCollection> getDate_collection_arr() {
        return date_collection_arr;
    }

    public static void setDate_collection_arr(ArrayList<CalendarCollection> date_collection_arr) {
        CalendarCollection.date_collection_arr = date_collection_arr;
    }

}
