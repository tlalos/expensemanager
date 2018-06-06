package com.example.tlalos.myapplication;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class FuncHelper {

    public static String CurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }

    public static Date StringToDate(String mDate){

        String str_date=mDate;
        DateFormat formatter ;
        Date date=null;
        formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            date = (Date)formatter.parse(str_date);

        } catch (ParseException e) {
            return null;
        }

        return date;
    }

    public static String DateToString(Date mDate){


        DateFormat formatter ;
        formatter = new SimpleDateFormat("dd/MM/yyyy");
        String strDate="";

        Calendar calendar = Calendar.getInstance();
        strDate = formatter.format(mDate);


        return strDate;
    }

    public static int GetMonth(Date mDate) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(mDate);
        return cal.get(Calendar.MONTH)+1;
    }

    public static int GetDay(Date mDate) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(mDate);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public static int GetYear(Date mDate) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(mDate);
        return cal.get(Calendar.YEAR);
    }


}
