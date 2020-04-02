package com.rentme.rentme.models;

import java.util.List;

public class Room {
    private User user;
    private List<String> images;
    private int status;
    private String facilities;
    private String property_type;
    private String city;
    private String location;
    private int price;
    private String description;
    private String title;
    private int user_id;
    private String created_at;
    private int id;
    public User getUser() {
        return user;
    }

    public List<String> getImages() {
        return images;
    }

    public int getStatus() {
        return status;
    }

    public String getFacilities() {
        return facilities;
    }

    public String getProperty_type() {
        return property_type;
    }

    public String getCity() {
        return city;
    }

    public String getLocation() {
        return location;
    }

    public int getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public int getId() {
        return id;
    }
}
