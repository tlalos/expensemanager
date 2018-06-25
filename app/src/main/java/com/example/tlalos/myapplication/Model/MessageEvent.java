package com.example.tlalos.myapplication.Model;

public class MessageEvent {

    public String mMessage;

    public int mMessageType;

    public MessageEvent(String message,int messagetype) {
        mMessage = message;
        mMessageType = messagetype;
    }
    public String getMessage() {
        return mMessage;
    }

    public int getMessageType() {
        return mMessageType;
    }


}
