package com.example.tlalos.myapplication.Data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.tlalos.myapplication.Util.Util;

import java.util.ArrayList;

public class DBHelper  extends SQLiteOpenHelper {

    private Context mContext;

    public DBHelper(Context context) {
        super(context, "expenses_app2.db", null,17);
        mContext=context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //ShowToast("ON CREATE");


        try {
            db.execSQL(
                    "create table expenses " +
                            "(_id integer primary key,expensecodeid integer, comments text,cdate text,value real)"
            );

            db.execSQL(
                    "create table expensetype " +
                            "(_id integer primary key,codeid integer,descr text)"
            );
        } catch (SQLiteException e) {
            // Catch block
        }



    }

    public void CheckDatabaseForUpdate() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res=null;

        try {
            res =  db.rawQuery( "select coalesce(dbversion,0) as dbversion from param", null );
        } catch (SQLiteException e) {
            // Catch block
            UpdateDatabase();
            return;
        }

        res.moveToFirst();

        Integer dbVersion=0;
        while(res.isAfterLast() == false){
            dbVersion=res.getInt(res.getColumnIndex("dbversion"));

            res.moveToNext();
        }

        if (dbVersion< Util.AppDBVersion){
            UpdateDatabase();
        }



    }

    public void UpdateDatabase() {
        SQLiteDatabase db = this.getReadableDatabase();


        ShowToast("DATABASE UPDATED TO VERSION "+Util.AppDBVersion);
        onCreate(db);


        try {
            db.execSQL(
                    "create table param " +
                            "(_id integer primary key,dbversion integer,monthlybudget real)"
            );

        } catch (SQLiteException e) {
            // Catch block
        }


        mCreateDBField(db,"expenses","cmonth","integer");
        mCreateDBField(db,"expenses","cyear","integer");

        mCreateDBField(db,"expenses","guid","text");
        mCreateDBField(db,"expenses","synced","integer");
        mCreateDBField(db,"expenses","deleted","integer");

        mCreateDBField(db,"expensetype","deleted","integer");


        int paramRows=TableRowCount("param");

        if (paramRows==0){
            db.execSQL("insert into param (dbversion) values ("+Util.AppDBVersion+")");
        }
        else {
            db.execSQL("update param set dbversion="+Util.AppDBVersion);
        }


    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    Boolean mCreateDBField(SQLiteDatabase db,String mTable,String mFieldName,String mFieldType){
        try {
            db.execSQL("ALTER TABLE "+mTable+" add column "+mFieldName+" "+mFieldType+"");
        } catch (SQLiteException e) {
            // Catch block
        }

        return true;
    }

    public ArrayList<String> getAllExpenses() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from expenses", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex("comments")));

            res.moveToNext();
        }
        return array_list;
    }


    public Integer TableRowCount(String mTableName) {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select count(*) as cnt from "+mTableName, null );
        res.moveToFirst();

        Integer rowCount=0;
        while(res.isAfterLast() == false){
            rowCount=res.getInt(res.getColumnIndex("cnt"));

            res.moveToNext();
        }
        return rowCount;
    }


    public Integer TableFieldMaxValue(String mTableName,String mField) {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select coalesce(max("+mField+"),0) as maxvalue from "+mTableName, null );
        res.moveToFirst();

        Integer maxValue=0;
        while(res.isAfterLast() == false){
            maxValue=res.getInt(res.getColumnIndex("maxvalue"));

            res.moveToNext();
        }
        return maxValue;
    }





    public void InsertDummyExpenseTypes(int mCodeId,String mDescr) {
        SQLiteDatabase db = this.getReadableDatabase();

        db.execSQL(
                "insert into expensetype " +
                        "(codeid,descr) values ('"+mCodeId+"','"+mDescr+"')"
        );
    }

    private void ShowToast(String text) {
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(mContext, text, duration);
        toast.show();

    }

}
