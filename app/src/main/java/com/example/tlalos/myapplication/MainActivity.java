package com.example.tlalos.myapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tlalos.myapplication.classes.Post;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String ENDPOINT = "http://192.168.1.47:45455/api/data/getmobiledata";
    private RequestQueue requestQueue;
    private Gson gson;


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
    TextView txtTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Expenses Manager "+FuncHelper.AppVersion);



        //ShowToast("ONCREATE ACTIVITY");

        mContext=getApplicationContext();

        cmbYear= (Spinner) findViewById(R.id.cmbMainYear);
        cmbMonth= (Spinner) findViewById(R.id.cmbMainMonth);
        txtTotal= (TextView) findViewById(R.id.txtMainTotal);


        //ShowToast("START");
        DBHelper dbHelper=new DBHelper(getApplicationContext());
        db=dbHelper.getReadableDatabase();
        dbHelper.CheckDatabaseForUpdate();



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
                //ShowToast("SELECTED YEAR:"+cyear);


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
                //ShowToast("SELECTED MONTH:"+cmonth);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





        FloatingActionButton fabAddNew;
        fabAddNew= (FloatingActionButton) findViewById(R.id.fabAddnewExpense);
        fabAddNew.setVisibility(View.INVISIBLE);

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


    void UpdateListTotals(Cursor c) {
        double mTotal=0;

        if (c.getCount()>0){
            c.moveToFirst();


            while(c.isAfterLast() == false){

                double value=0;
                value=c.getDouble(c.getColumnIndex("expensevalue"));
                mTotal+=value;

                c.moveToNext();
            }

            txtTotal.setText("Total :"+mTotal+"€");
        }

    }


    void UpdateListView() {

        try {

        String selectedYear=FuncHelper.RetSpinnerSelectedValue(cmbYear,"cyear");
        String selectedMonth=FuncHelper.RetSpinnerSelectedValue(cmbMonth,"cmonth");


        if (selectedYear==null || selectedYear=="") selectedYear="0";
        if (selectedMonth==null || selectedMonth=="") selectedMonth="0";


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

        UpdateListTotals(todoCursor);

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
            case R.id.main_menu_add_expense:
                Intent intent = new Intent(mContext, ExpenseDetailActivity.class);
                intent.putExtra("EditMode",0);
                intent.putExtra("RecId",0);
                startActivityForResult(intent, DETAIL_ACTIVITY_REQUEST_CODE);
                return true;

            case R.id.main_menu_Settings:
                GotoExpenseTypeAdmin();
                return true;
            case R.id.main_menu_testing:
                fetchPosts();
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


    private void fetchPosts() {

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();

        requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, ENDPOINT, onPostsLoaded, onPostsError);

        requestQueue.add(request);

    }

    private final Response.Listener<String> onPostsLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            //Log.i("PostActivity", response);

            Log.i("PostActivity", response.toString() );

            ShowToast(response.toString());

            try {
                List<Post> posts = Arrays.asList(gson.fromJson(response, Post[].class));

                Log.i("PostActivity", posts.size() + " posts loaded.");
                for (Post post : posts) {
                    Log.i("PostActivity", post.code + ": " + post.name);
                }

            } catch (Exception e) {
            // Catch block
                ShowToast(e.getMessage());
                Log.i("PostActivity", e.getMessage());
            }


        }
    };

    private final Response.ErrorListener onPostsError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("PostActivity", error.toString());
        }
    };

}
