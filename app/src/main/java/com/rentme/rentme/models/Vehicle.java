package com.rentme.rentme.models;

public class Vehicle {
    private User user;
    private int added_by;
    private String price;
    private String service_area;
    private String contact;
    private String owner_name;
    private String description;
    private String title;
    private int id;

    public User getUser() {
        return user;
    }

    public int getAdded_by() {
        return added_by;
    }

    public String getPrice() {
        return price;
    }

    public String getService_area() {
        return service_area;
    }

    public String getContact() {
        return contact;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }
}
