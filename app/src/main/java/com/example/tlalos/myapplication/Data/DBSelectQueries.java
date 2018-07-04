package com.example.tlalos.myapplication.Data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tlalos.myapplication.Model.Expense;
import com.example.tlalos.myapplication.Model.ExpenseItem;

import java.util.ArrayList;
import java.util.List;

public class DBSelectQueries {

    private SQLiteDatabase db=null;

    public DBSelectQueries(SQLiteDatabase db) {
        this.db = db;
    }


    public List<ExpenseItem> getAllExpenses(int year, int month) {
        List<ExpenseItem> list = new ArrayList<>();

        String mSQL="SELECT e._id,e.comments,e.cdate,e.cyear,e.cmonth,coalesce(e.value,0) as expensevalue,"+
                "e.guid,"+
                "e.expensecodeid,et.descr as expensedescr "+
                "FROM expenses e "+
                "left join expensetype et on et.codeid=e.expensecodeid "+
                "where "+
                "cyear="+year+" and "+
                "cmonth="+month+" and "+
                "coalesce(e.deleted,0)=0 "+
                "order by e._id desc";

        Cursor res =  db.rawQuery( mSQL, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            ExpenseItem item=new ExpenseItem();

            item.id=res.getLong(res.getColumnIndex("_id"));
            item.cDate=res.getString(res.getColumnIndex("cdate"));
            item.cMonth=res.getString(res.getColumnIndex("cmonth"));
            item.cYear=res.getString(res.getColumnIndex("cyear"));
            item.expenseCodeId=res.getInt(res.getColumnIndex("expensecodeid"));
            item.expenseDescr=res.getString(res.getColumnIndex("expensedescr"));
            item.guid=res.getString(res.getColumnIndex("guid"));
            item.value=res.getFloat(res.getColumnIndex("expensevalue"));
            item.comments=res.getString(res.getColumnIndex("comments"));


            list.add(item);

            res.moveToNext();
        }
        return list;
    }

}
