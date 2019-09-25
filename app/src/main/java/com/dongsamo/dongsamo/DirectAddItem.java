package com.dongsamo.dongsamo;

public class DirectAddItem {
    private String name, addr, call, location;

    public DirectAddItem(String name, String addr, String call, String location) {
        this.name = name;
        this.addr = addr;
        this.call = call;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getCall() {
        return call;
    }

    public void setCall(String call) {
        this.call = call;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
