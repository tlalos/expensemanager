package com.example.tlalos.myapplication.classes;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Post {

    @SerializedName("id")
    public long ID;

    //@SerializedName("date")
    //Date dateCreated;

    public String code;
    public String name;
    public String address;


}