package com.cssapp.cssapp;

public class DBEvent {

    Integer id;
    String name;
    Integer author;
    String date;
    String time;
    String location;
    String description;
    String duration;
    String picture;

    public DBEvent(){

    }

    public DBEvent(Integer id, String name, Integer author, String date, String time,
                   String location, String description, String duration, String picture){

        this.id = id;
        this.name = name;
        this.author = author;
        this.date = date;
        this.time = time;
        this.location = location;
        this.description = description;
        this.duration = duration;
        this.picture = picture;

    }

}
