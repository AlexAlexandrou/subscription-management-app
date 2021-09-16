package com.example.myapp;

import java.lang.reflect.Member;


//class containing setters and getters for adding and updating subscriptions
public class Subscriptions {
    private String Name;
    private String Description;
    private String Price;
    private String Date;
    private String Frequency;

    public Subscriptions(){
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getFrequency() {
        return Frequency;
    }

    public void setFrequency(String frequency) {
        Frequency = frequency;
    }


}
