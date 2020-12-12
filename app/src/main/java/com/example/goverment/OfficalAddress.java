package com.example.goverment;

import java.io.Serializable;


public class OfficalAddress implements Serializable {
    private String line1;
    private String city;
    private String state;
    private String zip;

    public OfficalAddress() {
    }

    public OfficalAddress(String line1, String city, String state, String zip) {
        this.line1 = line1;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }


    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    @Override
    public String toString() {
        return "Address : "+line1
                +","+city
                +","+state
                +","+zip;
    }
}
