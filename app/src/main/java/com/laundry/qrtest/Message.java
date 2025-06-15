package com.laundry.qrtest;


public class Message {
    private String username;
    private String text;
    private String type;
    private String filepath;
    private String lat;
    private String lng;

    public Message(String username, String text, String type,String filepath,String lat,String lng) {
        this.username = username;
        this.text = text;
        this.type = type;
        this.filepath = filepath;
        this.lat = lat;
        this.lng = lng;
    }


    public String getUsername() {
        return username;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }

    public String getFilepath() {
        return filepath;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }
}

