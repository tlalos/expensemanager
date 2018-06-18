package com.example.tlalos.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class FuncHelper {


    public static String ENDPOINT_ADDRESS = "http://192.168.1.47:45455";
    public static String ENDPOINT_GETDATA = ENDPOINT_ADDRESS+"/api/data/getmobiledata";
    public static String ENDPOINT_POSTDATA = ENDPOINT_ADDRESS+"/api/data/PostExpensesData";



    public static String AppVersion="1.3";
    public static int AppDBVersion=4;

    public static String CurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }


    public static int GetCurrentMonth() {
        Calendar calendar = Calendar.getInstance();

        return GetMonth(calendar.getTime());
    }


    public static int GetCurrentDay() {
        Calendar calendar = Calendar.getInstance();

        return GetDay(calendar.getTime());
    }


    public static int GetCurrentYear() {
        Calendar calendar = Calendar.getInstance();

        return GetYear(calendar.getTime());
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


    public static SimpleCursorAdapter LoadSpinner(Spinner Picker, Context mContext,String mQuery,String mViewField) {

        DBHelper dbHelper=new DBHelper(mContext);
        SQLiteDatabase db=dbHelper.getReadableDatabase();


        final Cursor cursor = db.rawQuery(mQuery, null);
        SimpleCursorAdapter mAdapter=null;

        if(cursor.getCount()>0){
            String[] from = new String[]{mViewField};
            // create an array of the display item we want to bind our data to
            int[] to = new int[]{android.R.id.text1};
            mAdapter = new SimpleCursorAdapter(mContext, android.R.layout.simple_spinner_item,
                    cursor, from, to,1);
            mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Picker.setAdapter(mAdapter);

        }

        return mAdapter;

    }


    public static String RetSpinnerSelectedValue(Spinner mSpinner,String mField) {

        String val="";
        try {
            Cursor cursor = (Cursor) mSpinner.getSelectedItem();
            val = cursor.getString(cursor.getColumnIndex(mField));
            } catch (Exception e) {
            // Catch block
        }


        return val;

    }

    public static void SetSpinnerSelectedValue(Spinner mSpinner,String mKeyField,String mKeyValue) {

        for (int i = 0; i < mSpinner.getCount(); i++) {
            Cursor value = (Cursor) mSpinner.getItemAtPosition(i);
            String mCurValue = value.getString(value.getColumnIndex(mKeyField));

            //if (mCurValue == mValue) {
            if (mCurValue!=null){
                if (mCurValue.equals(mKeyValue)){
                    mSpinner.setSelection(i);
                    return;
                }
            }

        }

    }



    public static String CursorToJSON(Cursor cursor) {

        cursor.moveToFirst();

        Gson gson = new Gson();
        ArrayList<Map> list = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            Map hashMap = new HashMap();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                hashMap.put(cursor.getColumnName(i), cursor.getString(i));
            }
            list.add(hashMap);
            cursor.moveToNext();
        }
        //System.out.println("\t\t\t" + gson.toJson(list));
        return gson.toJson(list);
    }


}
