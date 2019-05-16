package com.donsamo.dongsamo.firebase_control;

public class Store {
    private String name = "";
    private String url = "";
    private double star = 0;
    private double distance = 0;
    private boolean is_heart = false;

    public Store() {
        //default constructor
    }

    public Store(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public Store(String name, String url, double star, double distance, boolean is_heart) {
        this.name = name;
        this.url = url;
        this.star = star;
        this.distance = distance;
        this.is_heart = is_heart;
    }

    public double getStar() {
        return star;
    }

    public void setStar(double star) {
        this.star = star;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public boolean isIs_heart() {
        return is_heart;
    }

    public void setIs_heart(boolean is_heart) {
        this.is_heart = is_heart;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
