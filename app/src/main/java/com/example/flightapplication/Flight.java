package com.example.flightapplication;

public class Flight {
    private String id;
    private String from;
    private String to;
    private String departureDate;
    private String time;
    private int price;

    public Flight() {

    }

    public Flight(String from, String to, String date, String time, int price) {
        this.from = from;
        this.to = to;
        this.departureDate = date;
        this.time = time;
        this.price = price;
    }


    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
