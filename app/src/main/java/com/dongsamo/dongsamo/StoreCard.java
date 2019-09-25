package com.dongsamo.dongsamo;

import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;

public class StoreCard extends AppCompatActivity implements Serializable {
    private String name = "";
    private String category = "";
    private String url = "";
    private double x = 0;
    private double y = 0;
    private double star = 0;
    private boolean is_heart = false;

    public StoreCard() {
        //default constructor
    }

    public StoreCard(String name, String url) {
        this.name = name;
        this.url = url;
    }



    public StoreCard(String name, String category, String url, double x, double y, double star, boolean is_heart) {
        this.name = name;
        this.url = url;
        this.category = category;
        this.x = x;
        this.y = y;
        this.star = star;
        this.is_heart = is_heart;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public double getStar() {
        return star;
    }
    public void setStar(double star) {
        this.star = star;
    }
    public boolean isIs_heart() {
        return is_heart;
    }
    public void setIs_heart(boolean is_heart) {
        this.is_heart = is_heart;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

}
