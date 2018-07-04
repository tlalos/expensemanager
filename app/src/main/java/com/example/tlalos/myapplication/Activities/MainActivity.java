package com.example.tlalos.myapplication.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tlalos.myapplication.Data.DBHelper;
import com.example.tlalos.myapplication.Model.Expense;
import com.example.tlalos.myapplication.Model.MessageEvent;
import com.example.tlalos.myapplication.Model.Post;
import com.example.tlalos.myapplication.R;
import com.example.tlalos.myapplication.Data.TodoCursorAdapter;
import com.example.tlalos.myapplication.Util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    private RequestQueue requestQueue;
    private Gson gson;


    private static final int DETAIL_ACTIVITY_REQUEST_CODE = 0;

    private TodoCursorAdapter todoAdapter=null;
    private Cursor todoCursor=null;
    private Boolean bypassComboYearOnSelect=false;
    private Boolean bypassComboMonthOnSelect=false;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    ListView lvItems;
    Spinner cmbYear;
    Spinner cmbMonth;
    SQLiteDatabase db=null;
    Context mContext;
    TextView txtTotal;

    private Button cmdProgressClose;
    private TextView txtProgressMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Expenses Manager "+ Util.AppVersion);



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

        Util.SetSpinnerSelectedValue(cmbYear,"cyear",Integer.toString(Util.GetCurrentYear()));
        Util.SetSpinnerSelectedValue(cmbMonth,"cmonth",Integer.toString(Util.GetCurrentMonth()));




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
        Util.LoadSpinner(cmbYear,this,"SELECT _id,cyear FROM expenses where coalesce(deleted,0)=0 group by cyear order by cyear desc","cyear");

        Util.LoadSpinner(cmbMonth,this,"SELECT _id,cmonth FROM expenses where coalesce(deleted,0)=0 group by cmonth order by cmonth asc","cmonth");


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
            DecimalFormat df2 = new DecimalFormat(".##");


            txtTotal.setText("Total :"+df2.format(mTotal)+"€");
        }

    }


    void UpdateListView() {

        try {

        String selectedYear=Util.RetSpinnerSelectedValue(cmbYear,"cyear");
        String selectedMonth=Util.RetSpinnerSelectedValue(cmbMonth,"cmonth");


        if (selectedYear==null || selectedYear=="") selectedYear="0";
        if (selectedMonth==null || selectedMonth=="") selectedMonth="0";


        String mSQL="SELECT e._id,e.comments,e.cdate,coalesce(e.value,0) as expensevalue,et.descr as expensedescr "+
                    "FROM expenses e "+
                    "left join expensetype et on et.codeid=e.expensecodeid "+
                    "where "+
                    "cyear="+selectedYear+" and "+
                    "cmonth="+selectedMonth+" and "+
                    "coalesce(e.deleted,0)=0 "+
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
                String pyear=Util.RetSpinnerSelectedValue(cmbYear,"cyear");
                String pmonth=Util.RetSpinnerSelectedValue(cmbMonth,"cmonth");
                //load pickers
                LoadPickers();


                Util.SetSpinnerSelectedValue(cmbYear,"cyear",pyear);
                Util.SetSpinnerSelectedValue(cmbMonth,"cmonth",pmonth);

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
                //GotoExpenseTypeAdmin();
                Intent intent2 = new Intent(mContext, RecyclerViewActivity.class);
                startActivity(intent2);
                return true;
            case R.id.main_menu_testing:
                try {
                    doPostDataProcess();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return true;

            case R.id.main_menu_markunsynced:
              db.execSQL("update expenses set synced=0");
              return true;

            //case R.id.main_menu_updateguid:
              //  UpdateNewGUIDs();
                //return true;

            //case R.id.main_menu_deleteall:
              //  db.execSQL("delete from expenses");
                //db.execSQL("delete from expensetype");
                //UpdateListView();
                //return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void ShowProgressDialog() {
        //OLD WAY AS INTENT
        //Intent intent = new Intent(mContext, ProgressActivity.class);
        //intent.putExtra("Message","Delete Expense ?");
        //startActivity(intent);

        dialogBuilder=new AlertDialog.Builder(this);
        View view= getLayoutInflater().inflate(R.layout.activity_progress,null);

        cmdProgressClose=(Button) view.findViewById(R.id.cmdProgressClose);
        txtProgressMessage=(TextView) view.findViewById(R.id.lblProgressMessage);


        dialogBuilder.setView(view);
        dialog=dialogBuilder.create();
        dialog.show();


        cmdProgressClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }


    private void UpdateNewGUIDs() {

        Cursor res=null;

        try {
            res =  db.rawQuery( "select _id as id from expenses where coalesce(guid,'')=''", null );
        } catch (SQLiteException e) {
            // Catch block

        }


        res.moveToFirst();

        while(res.isAfterLast() == false){
          int id=res.getInt(res.getColumnIndex("id"));

            db.execSQL("update expenses set guid=lower(hex(randomblob(16))) where _id="+id);

            res.moveToNext();
        }

        ShowToast("GUIDs updated");

    }

    private void ShowToast(String text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

    }



    private void doPostDataProcess() throws JSONException, InterruptedException {
        ShowProgressDialog();
        PostData_Expenses();
    }


    private void PostData_Expenses() throws JSONException {

        //EventBus.getDefault().postSticky(new MessageEvent("Synchronizing expenses to cloud...",0));
        txtProgressMessage.setText("Synchronizing expenses to cloud...");

        Uri myUI = Uri.parse (Util.ENDPOINT_POST_EXPENSES_DATA).buildUpon().build();


        Cursor c =  db.rawQuery( "select _id as id,"+
                "cdate,"+
                "coalesce(cyear,0) as cyear,"+
                "coalesce(cmonth,0) as cmonth,"+
                "coalesce(expensecodeid,0) as expensecodeid,"+
                "cdate as category,"+
                "coalesce(comments,'') as comments, "+
                "coalesce(value,0) as value,"+
                "coalesce(deleted,0) as deleted,"+
                "coalesce(guid,'') as guid "+
                "from expenses "+
                "where coalesce(synced,0)=0", null );

        String jSONData=Util.CursorToJSON(c);
        String your_string_json=jSONData;



        RequestQueue queue = Volley.newRequestQueue(this);


        JsonArrayRequest jsonobj = new JsonArrayRequest(Request.Method.POST, myUI.toString(),new JSONArray(your_string_json),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //ShowToast("OK RESPONSE:"+response.toString());
                        try {

                            //JSONObject jsonObjectResult = response.getJSONObject(0);
                            //String rescode=jsonObjectResult.getString("result").toLowerCase();
                            String rescode=response.getJSONObject(0).getString("result").toLowerCase();
                            String resDescr=response.getJSONObject(1).getString("resultdescr").toLowerCase();;
                            //ShowToast(rescode);

                            if (rescode.equals("error")) {
                                //EventBus.getDefault().postSticky(new MessageEvent("Post Expenses Error:"+resDescr,100));
                                txtProgressMessage.setText("Post Expenses Error:"+resDescr);
                                cmdProgressClose.setEnabled(true);
                                //ShowToast(resDescr);

                            }
                            else {

                                //update synced flag
                                db.execSQL("update expenses set synced=1 where coalesce(synced,0)=0");

                                //deleted any marked deleted
                                db.execSQL("delete from expenses where coalesce(deleted,0)=1");



                                try {
                                    PostData_ExpenseTypes();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //EventBus.getDefault().postSticky(new MessageEvent("Post Expenses Error:"+error.getLocalizedMessage(),100));
                        txtProgressMessage.setText("Post Expenses Error:"+error.getLocalizedMessage());
                        cmdProgressClose.setEnabled(true);

                    }
                }
        ){
            //here I want to post data to sever
        };


        jsonobj.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonobj);




    }



    private void PostData_ExpenseTypes() throws JSONException {

        //EventBus.getDefault().postSticky(new MessageEvent("Syncing expense types to cloud...",0));
        txtProgressMessage.setText("Syncing expense types to cloud...");

        Uri myUI = Uri.parse (Util.ENDPOINT_POST_EXPENSETYPE_DATA).buildUpon().build();

        Cursor c =  db.rawQuery( "select _id as id,"+
                "codeid,"+
                "coalesce(descr,'') as descr,"+
                "coalesce(deleted,0) as deleted "+
                "from expensetype", null );

        String jSONData=Util.CursorToJSON(c);
        String your_string_json=jSONData;



        RequestQueue queue = Volley.newRequestQueue(this);


        JsonArrayRequest jsonobj = new JsonArrayRequest(Request.Method.POST, myUI.toString(),new JSONArray(your_string_json),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //ShowToast("OK RESPONSE:"+response.toString());

                        //deleted any marked deleted
                        db.execSQL("delete from expensetype where coalesce(deleted,0)=1");

                        //get posts
                        try {
                            fetchExpenses();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //ShowToast("ERROR RESPONSE:"+error.getMessage());
                        //EventBus.getDefault().postSticky(new MessageEvent("Post Expense Types Error:"+error.getLocalizedMessage(),100));
                        txtProgressMessage.setText("Post Expense Types Error:"+error.getLocalizedMessage());
                        cmdProgressClose.setEnabled(true);

                    }
                }
        ){
            //here I want to post data to sever
        };


        jsonobj.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonobj);




    }






    private void fetchExpenses() throws JSONException {

        //EventBus.getDefault().postSticky(new MessageEvent("Getting expenses from cloud...",0));
        txtProgressMessage.setText("Getting expenses from cloud...");

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();


        Uri myUI = Uri.parse (Util.ENDPOINT_GETDATA).buildUpon()
                .appendQueryParameter("requestcode","expenses")
                .appendQueryParameter("devicecode","")
                .appendQueryParameter("param","")
                .build();


        Cursor c =  db.rawQuery( "select "+
                "guid "+
                "from expenses "+
                "where "+
                "coalesce(deleted,0)=0", null );

        String jSONData=Util.CursorToJSON(c);
        String your_string_json=jSONData;





        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        JsonArrayRequest stringRequest = new JsonArrayRequest (Request.Method.POST, myUI.toString(),new JSONArray(your_string_json),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // your response
                        //Log.i("PostActivity", response.toString() );

                        //ShowToast(response.toString());

                        List<Expense> expenses= Arrays.asList(gson.fromJson(response.toString(), Expense[].class));


                        //update expenses
                        UpdateExpensesFromSync(expenses);

                        //EventBus.getDefault().postSticky(new MessageEvent("Synchronization finished successfully",100));
                        txtProgressMessage.setText("Synchronization finished successfully");
                        cmdProgressClose.setEnabled(true);

                    }
                }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                //Log.e("PostActivity", error.toString());
                //EventBus.getDefault().postSticky(new MessageEvent("Fetch Expenses Error:"+error.getLocalizedMessage(),100));
                txtProgressMessage.setText("Fetch Expenses Error:"+error.getLocalizedMessage());
                cmdProgressClose.setEnabled(true);

            }
        }){

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }



    private void UpdateExpensesFromSync(List<Expense> expenses) {


        try {

            for (Expense expense: expenses) {

                String mSQL="SELECT e.guid "+
                        "FROM expenses e "+
                        "where guid='"+expense.guid+"'";
                Cursor c= db.rawQuery(mSQL, null);

                Boolean insertNew=false;
                if (c.getCount()>0)
                    insertNew = false;
                else
                    insertNew=true;

                if (insertNew){ //insert new

                    String mSQLInsert;
                    mSQLInsert="insert into expenses " +
                            "(cdate,cyear,cmonth,expensecodeid,value, comments,synced,guid) "+
                            "values "+
                            "("+
                            "'"+expense.cdate+"'," +
                            "'"+expense.cyear+"'," +
                            "'"+expense.cmonth+"'," +
                            "'"+ expense.expensecodeid+"'," +
                            "'"+ expense.value+"'," +
                            "'"+expense.comments+"'," +
                            "'1'," +
                            "'"+expense.guid+"'" +
                            ")";
                    db.execSQL(mSQLInsert);

                }


                //expensetype
                mSQL="SELECT et.codeid "+
                      "FROM expensetype et "+
                      "where et.codeid='"+expense.expensecodeid+"'";
                Cursor ctype= db.rawQuery(mSQL, null);

                Boolean insertNewType=false;
                if (ctype.getCount()>0)
                    insertNewType= false;
                else
                    insertNewType=true;

                if (insertNewType) {

                    mSQL="insert into expensetype "+
                         "(codeid,descr) "+
                         "values "+
                         "(" +
                         "'"+expense.expensecodeid+"',"+
                         "'"+expense.expensedescr+"' "+
                         ")";
                    db.execSQL(mSQL);
                }


            }


            //loadpickers and listview
            bypassComboYearOnSelect=true;
            bypassComboMonthOnSelect=true;
            //keep old values
            String pyear=Util.RetSpinnerSelectedValue(cmbYear,"cyear");
            String pmonth=Util.RetSpinnerSelectedValue(cmbMonth,"cmonth");
            //load pickers
            LoadPickers();


            Util.SetSpinnerSelectedValue(cmbYear,"cyear",pyear);
            Util.SetSpinnerSelectedValue(cmbMonth,"cmonth",pmonth);


            UpdateListView();


        } catch (Exception e) {
            // Catch block
            ShowToast(e.getMessage());
            Log.i("PostActivity", e.getMessage());
        }

    }

    private void fetchPosts2() {

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();


        Uri myUI = Uri.parse (Util.ENDPOINT_GETDATA).buildUpon()
                .appendQueryParameter("requestcode","allcustomers")
                .appendQueryParameter("devicecode","")
                .appendQueryParameter("param","")
                .build();

        //Uri myUI = Uri.parse (Util.ENDPOINT_GETDATA).buildUpon()
                //.appendQueryParameter("requestcode","singlecustomer")
                //.appendQueryParameter("devicecode","")
                //.appendQueryParameter("param","00-00-001")
                //.build();


        requestQueue = Volley.newRequestQueue(this);


        StringRequest request = new StringRequest(Request.Method.GET, myUI.toString(), onPostsLoaded, onPostsError);


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
