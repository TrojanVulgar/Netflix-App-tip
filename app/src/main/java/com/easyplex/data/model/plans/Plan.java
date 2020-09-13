package com.easyplex.data.model.plans;

import com.google.gson.annotations.SerializedName;

public class Plan {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private int title;


    @SerializedName("price")
    private String price;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

}
