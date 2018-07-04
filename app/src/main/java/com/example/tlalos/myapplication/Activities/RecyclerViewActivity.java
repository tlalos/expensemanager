package com.example.tlalos.myapplication.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.tlalos.myapplication.Data.DBHelper;
import com.example.tlalos.myapplication.Data.DBSelectQueries;
import com.example.tlalos.myapplication.Model.Expense;
import com.example.tlalos.myapplication.Model.ExpenseItem;
import com.example.tlalos.myapplication.Model.ExpenseListItem;
import com.example.tlalos.myapplication.R;
import com.example.tlalos.myapplication.UI.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
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


        List<ExpenseItem> expenses;
        expenses=dbQ.getAllExpenses(2018,6);

        listitems=new ArrayList<>();

        for (int i=0;i<expenses.size();i++){

            ExpenseListItem item=new ExpenseListItem();
            item.setField1(expenses.get(i).expenseDescr);
            item.setField2(expenses.get(i).cDate);
            item.setValue(expenses.get(i).value);

            listitems.add(item);
        }


        recyclerViewAdapter=new RecyclerViewAdapter(this,listitems);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();

    }
}
