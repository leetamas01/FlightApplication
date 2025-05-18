package com.example.flightapplication;

public class Booking {

    public String userId;
    public String from;
    public String to;
    public String date;
    public String time;
    public int price;
    public long timestamp;

    public Booking() {}

    public Booking(String userId, String from, String to, String date, String time, int price) {
            this.userId = userId;
            this.from = from;
            this.to = to;
            this.date = date;
            this.time = time;
            this.price = price;
            this.timestamp = System.currentTimeMillis();
    }
}
