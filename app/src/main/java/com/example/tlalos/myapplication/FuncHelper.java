package com.example.tlalos.myapplication;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FuncHelper {

    public static String CurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }
}
