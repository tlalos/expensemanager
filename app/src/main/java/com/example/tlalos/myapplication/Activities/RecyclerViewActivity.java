package com.example.tlalos.myapplication.Activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.tlalos.myapplication.Data.DBHelper;
import com.example.tlalos.myapplication.Data.DBSelectQueries;
import com.example.tlalos.myapplication.Model.Expense;
import com.example.tlalos.myapplication.Model.ExpenseItem;
import com.example.tlalos.myapplication.Model.ExpenseListItem;
import com.example.tlalos.myapplication.R;
import com.example.tlalos.myapplication.UI.RecyclerViewAdapter;
import com.example.tlalos.myapplication.Util.Util;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewActivity extends AppCompatActivity {

    private static final int DETAIL_ACTIVITY_REQUEST_CODE = 0;

    private RecyclerView recyclerView;
    //private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<ExpenseListItem> listitems;
    private DBSelectQueries dbQ=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        DBHelper dbHelper=new DBHelper(getApplicationContext());
        dbQ=new DBSelectQueries(dbHelper.getReadableDatabase());


        recyclerView=(RecyclerView) findViewById(R.id.recyclerViewID);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        loadExpensesList();


        recyclerViewAdapter=new RecyclerViewAdapter(this,listitems);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();

    }


    private void loadExpensesList(){
        List<ExpenseItem> expenses;
        expenses=dbQ.getAllExpenses(2018,6);

        listitems=new ArrayList<>();

        for (int i=0;i<expenses.size();i++){

            ExpenseListItem item=new ExpenseListItem();
            item.setId(expenses.get(i).id);
            item.setField1(expenses.get(i).expenseDescr);
            item.setField2(expenses.get(i).cDate);
            item.setValue(Float.toString(expenses.get(i).value)+'€');


            listitems.add(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check that it is the SecondActivity with an OK result
        if (requestCode == DETAIL_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                loadExpensesList();
                recyclerViewAdapter.updateList(listitems);


                // get String data from the returning Intent
                //String returnString = data.getStringExtra("keyName");


                //View parentLayout = findViewById(android.R.id.content);
                //Snackbar.make(parentLayout,"OnActivityResult",Snackbar.LENGTH_LONG).show();


            }
        }
    }

}
