package com.example.tlalos.myapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {

    private static final int DETAIL_ACTIVITY_REQUEST_CODE = 0;

    private TodoCursorAdapter todoAdapter=null;
    private Cursor todoCursor=null;
    private Boolean bypassComboYearOnSelect=false;
    private Boolean bypassComboMonthOnSelect=false;

    ListView lvItems;
    Spinner cmbYear;
    Spinner cmbMonth;
    SQLiteDatabase db=null;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Expenses Manager");

        //ShowToast("ONCREATE ACTIVITY");

        mContext=getApplicationContext();

        cmbYear= (Spinner) findViewById(R.id.cmbMainYear);
        cmbMonth= (Spinner) findViewById(R.id.cmbMainMonth);


        //ShowToast("START");
        DBHelper dbHelper=new DBHelper(getApplicationContext());
        db=dbHelper.getReadableDatabase();

        //load pickers
        LoadPickers();

        FuncHelper.SetSpinnerSelectedValue(cmbYear,"cyear",Integer.toString(FuncHelper.GetCurrentYear()));
        FuncHelper.SetSpinnerSelectedValue(cmbMonth,"cmonth",Integer.toString(FuncHelper.GetCurrentMonth()));




        // Find ListView to populate
        lvItems = (ListView) findViewById(R.id.lsvItems);
        UpdateListView();



        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //todoAdapter.getItem(position);
                //ShowToast(Long.toString(id));

                Intent intent = new Intent(mContext, ExpenseDetailActivity.class);
                intent.putExtra("EditMode",1);
                intent.putExtra("RecId",id);
                startActivityForResult(intent, DETAIL_ACTIVITY_REQUEST_CODE);



            }
        });


        cmbYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (bypassComboYearOnSelect==true) {
                    bypassComboYearOnSelect=false;
                    return;
                }

                Cursor c = (Cursor)parent.getItemAtPosition(position);
                String cyear = c.getString(c.getColumnIndexOrThrow("cyear"));

                UpdateListView();
                ShowToast("SELECTED YEAR:"+cyear);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        cmbMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (bypassComboMonthOnSelect==true) {
                    bypassComboMonthOnSelect=false;
                    return;
                }

                Cursor c = (Cursor)parent.getItemAtPosition(position);
                String cmonth = c.getString(c.getColumnIndexOrThrow("cmonth"));

                UpdateListView();
                ShowToast("SELECTED MONTH:"+cmonth);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





        FloatingActionButton fabAddNew;
        fabAddNew= (FloatingActionButton) findViewById(R.id.fabAddnewExpense);

        fabAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, ExpenseDetailActivity.class);
                intent.putExtra("EditMode",0);
                intent.putExtra("RecId",0);
                startActivityForResult(intent, DETAIL_ACTIVITY_REQUEST_CODE);




            }
        });


    }


    void DisablePickerOnListener(Spinner mSpinner) {

        AdapterView.OnItemSelectedListener onItemSelectedListener = mSpinner.getOnItemSelectedListener();
        mSpinner.setOnItemSelectedListener(null);
        mSpinner.setSelection(0);
        mSpinner.setOnItemSelectedListener(onItemSelectedListener);

    }

    void LoadPickers() {
        FuncHelper.LoadSpinner(cmbYear,this,"SELECT _id,cyear FROM expenses group by cyear order by cyear desc","cyear");

        FuncHelper.LoadSpinner(cmbMonth,this,"SELECT _id,cmonth FROM expenses group by cmonth order by cmonth asc","cmonth");


    }



    void UpdateListView() {

        try {

        String selectedYear=FuncHelper.RetSpinnerSelectedValue(cmbYear,"cyear");
        String selectedMonth=FuncHelper.RetSpinnerSelectedValue(cmbMonth,"cmonth");


        String mSQL="SELECT e._id,e.comments,e.cdate,coalesce(e.value,0) as expensevalue,et.descr as expensedescr "+
                    "FROM expenses e "+
                    "left join expensetype et on et.codeid=e.expensecodeid "+
                    "where "+
                    "cyear="+selectedYear+" and "+
                    "cmonth="+selectedMonth+" "+
                    "order by e._id desc";
        todoCursor = db.rawQuery(mSQL, null);
        todoAdapter = new TodoCursorAdapter(this, todoCursor);
        // Attach cursor adapter to the ListView
        lvItems.setAdapter(todoAdapter);

        } catch (SQLiteException e) {
            ShowToast(e.getMessage());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check that it is the SecondActivity with an OK result
        if (requestCode == DETAIL_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // get String data from the returning Intent
                String returnString = data.getStringExtra("keyName");

                //enimerose to listview
                UpdateListView();


                bypassComboYearOnSelect=true;
                bypassComboMonthOnSelect=true;
                //keep old values
                String pyear=FuncHelper.RetSpinnerSelectedValue(cmbYear,"cyear");
                String pmonth=FuncHelper.RetSpinnerSelectedValue(cmbMonth,"cmonth");
                //load pickers
                LoadPickers();


                FuncHelper.SetSpinnerSelectedValue(cmbYear,"cyear",pyear);
                FuncHelper.SetSpinnerSelectedValue(cmbMonth,"cmonth",pmonth);

                UpdateListView();



            }
        }
    }

    void GotoExpenseTypeAdmin() {
        Intent intent = new Intent(mContext, ExpenseTypeActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.main_menu_Favorite:
                //newGame();
                return true;
            case R.id.main_menu_Settings:
                GotoExpenseTypeAdmin();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void ShowToast(String text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

    }
}
