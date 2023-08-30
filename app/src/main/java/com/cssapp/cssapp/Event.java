//Java Class that defines list_item and stores the data for every event.

package com.cssapp.cssapp;

public class Event {
    // Store the name of the event
    private String eName;
    // Store the description of the event
    private String eDescription;
    // Store the author of the event
    private String eAuthor;

    private String eId;
    private Integer eHide;

    Event(String eName, String eDescription, String eAuthor, String eId, Integer hide) {
        this.eName = eName;
        this.eDescription = eDescription;
        this.eAuthor = eAuthor;
        this.eId = eId;
        this.eHide = hide;
    }

    public Integer getHide(){return eHide;}
    public String getName() {
        return eName;
    }

    public String getDescription() {
        return eDescription;
    }

    String getAuthor() {
        return eAuthor;
    }

    String getId() {
        return eId;
    }
}