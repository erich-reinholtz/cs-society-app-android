package com.cssapp.cssapp;

public class DBComment {

    Integer eventID;
    Integer userID;
    String comment;
    String time;



    public DBComment(){

    }

    public DBComment(Integer eventID, Integer userID, String comment, String time){

        this.eventID = eventID;
        this.userID = userID;
        this.comment = comment;
        this.time = time;

    }

}
