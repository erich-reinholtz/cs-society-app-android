package com.cssapp.cssapp;

import java.util.HashMap;
import java.util.Map;

public class DBUser {

    public Integer id;
    public String name;
    public String surname;
    public Integer year;
    //    public String[] tags;
    public String picture;
    public Integer permission;
    public String google;

    public DBUser() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public DBUser(Integer id, String name, String surname, Integer year, String picture, Integer permission, String google) {

        this.id = id;
        this.name = name;
        this.surname = surname;
        this.year = year;
        //this.tags = tags;
        this.picture = picture;
        this.permission = permission;
        this.google = google;

    }


    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("surname", surname);
        result.put("year", year);
        //result.put("tags", tags);
        result.put("picture", picture);
        result.put("permission", permission);
        result.put("google", google);

        return result;
    }


}

