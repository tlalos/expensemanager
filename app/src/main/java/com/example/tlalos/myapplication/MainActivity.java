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

        //load picker
        final Cursor cursor = db.rawQuery("SELECT  * FROM expensetype", null);
        if(cursor.getCount()>0){
            String[] from = new String[]{"descr"};
            // create an array of the display item we want to bind our data to
            int[] to = new int[]{android.R.id.text1};
            SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item,
                    cursor, from, to,1);
            mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cmbYear.setAdapter(mAdapter);
            cmbMonth.setAdapter(mAdapter);

        }



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

    void UpdateListView() {

        try {


        String mSQL="SELECT e._id,e.comments,e.cdate,coalesce(e.value,0) as expensevalue,et.descr as expensedescr "+
                    "FROM expenses e "+
                    "left join expensetype et on et.codeid=e.expensecodeid "+
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
