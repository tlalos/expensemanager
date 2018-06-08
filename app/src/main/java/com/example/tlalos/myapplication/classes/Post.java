package com.example.tlalos.myapplication.classes;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Post {

    @SerializedName("id")
    public long ID;

    @SerializedName("date")
    Date dateCreated;

    public String title;
    String author;
    String url;
    String body;

}