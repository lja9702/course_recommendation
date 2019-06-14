package com.dongsamo.dongsamo;

import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;

public class StoreCard extends AppCompatActivity implements Serializable {

    private String name;
    private double star;
    private double distance;
    private boolean is_heart;
    private String img_url;

    public StoreCard(String img_url, String name, double star, double distance, boolean is_heart) {
        this.img_url = img_url;
        this.name = name;
        this.star = star;
        this.distance = distance;
        this.is_heart = is_heart;
    }

    public String getImg_url(){
        return img_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
