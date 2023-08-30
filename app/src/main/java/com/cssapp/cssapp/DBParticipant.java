package com.cssapp.cssapp;

import java.util.regex.Pattern;

public class DBParticipant {

    Integer personID;
    Integer eventID;

    public DBParticipant(){

    }

    public DBParticipant(Integer personID, Integer eventID){

        this.personID = personID;
        this.eventID = eventID;

    }

}
