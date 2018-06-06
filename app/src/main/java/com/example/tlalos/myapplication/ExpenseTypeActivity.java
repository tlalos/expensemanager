package com.example.tlalos.myapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class ExpenseTypeActivity extends AppCompatActivity {

    private ExpenseTypeAdapter expenseTypeAdapter=null;
    private Cursor expenseTypeCursor=null;


    private static final int EXPENSETYPECART_ACTIVITY_REQUEST_CODE = 0;



    ListView lvItems;
    FloatingActionButton fabAddNewExpenseType;
    SQLiteDatabase db=null;
    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_type);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Expense Types");

        mContext=getApplicationContext();

        DBHelper dbHelper=new DBHelper(getApplicationContext());
        db=dbHelper.getReadableDatabase();

        fabAddNewExpenseType=(FloatingActionButton) findViewById(R.id.fabAddnewExpenseType);

        lvItems = (ListView) findViewById(R.id.lsvExpenseTypeItems);
        UpdateListView();


        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //todoAdapter.getItem(position);
                //ShowToast(Long.toString(id));

                Intent intent = new Intent(mContext, ExpenseTypeCartActivity.class);
                intent.putExtra("EditMode",1);
                intent.putExtra("RecId",id);
                startActivityForResult(intent, EXPENSETYPECART_ACTIVITY_REQUEST_CODE);



            }
        });

        fabAddNewExpenseType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, ExpenseTypeCartActivity.class);
                intent.putExtra("EditMode",0);
                intent.putExtra("RecId",0);
                startActivityForResult(intent, EXPENSETYPECART_ACTIVITY_REQUEST_CODE);

            }
        });





    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check that it is the SecondActivity with an OK result
        if (requestCode == EXPENSETYPECART_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // get String data from the returning Intent
                String returnString = data.getStringExtra("keyName");

                //enimerose to listview
                UpdateListView();

            }
        }
    }


    void UpdateListView() {

        try {


            String mSQL="SELECT e._id,e.descr,e.codeid "+
                    "FROM expensetype e "+
                    "order by e.codeid desc";
            expenseTypeCursor = db.rawQuery(mSQL, null);
            expenseTypeAdapter = new ExpenseTypeAdapter(this, expenseTypeCursor );
            // Attach cursor adapter to the ListView
            lvItems.setAdapter(expenseTypeAdapter );

        } catch (SQLiteException e) {
            ShowToast(e.getMessage());
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    private void ShowToast(String text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

    }

}
