package com.example.tlalos.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ExpenseDetailActivity extends AppCompatActivity  {

    SQLiteDatabase db=null;
    Button cmdSelectDate;
    Button cmdDelete;
    Spinner cmbExpenseType;
    EditText txtComments;
    EditText txtDate;
    EditText txtValue;

    int editMode;
    long recId=0;
    Context mContext;


    private static final int ALERT_ACTIVITY_REQUEST_CODE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Expense Detail");


        mContext=getApplicationContext();


        cmdSelectDate=findViewById(R.id.cmdSelectDate);
        cmdDelete=findViewById(R.id.cmdDeleteExpense);
        cmbExpenseType=findViewById(R.id.cmbExpenseType);
        txtComments=findViewById(R.id.txtComments);
        txtDate=findViewById(R.id.txtDate);
        txtValue=findViewById(R.id.txtValue);


        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            editMode=extras.getInt("EditMode") ;
            if (editMode==1){
                recId=extras.getLong("RecId") ;
                //ShowToast(Long.toString(recId));
            }

        }


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
            cmbExpenseType.setAdapter(mAdapter);

        }



        cmbExpenseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor)parent.getItemAtPosition(position);
                int db_row_id = c.getInt(c.getColumnIndexOrThrow("_id"));
                int db_row_codeid = c.getInt(c.getColumnIndexOrThrow("codeid"));
                String dbField = c.getString(c.getColumnIndexOrThrow("descr"));
                //ShowToast(Integer.toString(db_row_id)+":"+dbField);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





        cmdSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment newFragment = new DatePickerFragment();

                //pass parameters to fragment dialog
                Bundle args = new Bundle();
                args.putInt("EditText",R.id.txtDate);
                newFragment.setArguments(args);

                newFragment.show(getSupportFragmentManager(), "datePicker");


            }
        });


        cmdDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, AlertActivity.class);
                intent.putExtra("Message","Delete Expense ?");
                startActivityForResult(intent, ALERT_ACTIVITY_REQUEST_CODE);

            }
        });



        //edit mode stuff
        if(editMode==0){ //new
            mLoadNew();
        }
        else { //edit
            mLoad();
        }

    }


    void mDelete() {
        db.execSQL("delete from expenses where _id="+recId);

        ShowToast("Expense deleted successfully");
        Intent intent = new Intent();
        intent.putExtra("keyName", "TEST RESULT DATA");
        setResult(RESULT_OK, intent);
        finish();

    }



    void mLoadNew() {

        txtDate.setText(FuncHelper.CurrentDate());
        txtValue.setText("0");


        cmdDelete.setVisibility(View.INVISIBLE);

    }

    void mLoad() {
        String mSQL="SELECT "+
                    "e._id,"+
                    "coalesce(e.value,0) as value,"+
                    "coalesce(e.comments,'') as comments,"+
                    "e.cdate,coalesce(e.expensecodeid,0) as expensecodeid "+
                    "from expenses e "+
                    "where "+
                    "e._id="+recId;

        Cursor c=null;

        try
        {
            c= db.rawQuery(mSQL, null);

        } catch (SQLiteException e) {
            ShowToast(e.getMessage());

        }


        if(c.getCount()>0){
            c.moveToFirst();

            txtDate.setText(c.getString(c.getColumnIndexOrThrow("cdate")));
            txtValue.setText(Float.toString(c.getFloat(c.getColumnIndexOrThrow("value"))));
            txtComments.setText(c.getString(c.getColumnIndexOrThrow("comments")));

            setSpinnerSelectedValue(cmbExpenseType,"codeid",c.getString(c.getColumnIndexOrThrow("expensecodeid")));
        }


    }




    String retSpinnerSelectedValue(Spinner mSpinner,String mField) {

        Cursor cursor = (Cursor) mSpinner.getSelectedItem();
        String val = cursor.getString(cursor.getColumnIndex(mField));

        return val;
    }



    void mSave() {

        String str_date=txtDate.getText().toString();
        //ShowToast(FuncHelper.StringToDate(str_date).toString());




        try {

            String mExpendId=retSpinnerSelectedValue(cmbExpenseType,"codeid");

            String mSQL="";
            if (editMode==1){//edit mode
                mSQL="update expenses "+
                     "set "+
                     "cdate='"+txtDate.getText()+"',"+
                     "expensecodeid='"+mExpendId+"',"+
                     "value='"+txtValue.getText()+"',"+
                     "comments='"+txtComments.getText()+"' "+
                     "where _id="+recId;
            }
            else // new mode
            {

                mSQL="insert into expenses " +
                        "(cdate,expensecodeid,value, comments) "+
                        "values "+
                        "("+
                        "'"+txtDate.getText()+"'," +
                        "'"+mExpendId+"'," +
                        "'"+txtValue.getText()+"'," +
                        "'"+txtComments.getText()+"'" +
                        ")";
            }


            db.execSQL(mSQL);

            Intent intent = new Intent();
            intent.putExtra("keyName", "TEST RESULT DATA");
            setResult(RESULT_OK, intent);
            finish();



        } catch (SQLiteException e) {
            // Catch block
            ShowToast(e.getMessage());
        }




    }

    void setSpinnerSelectedValue(Spinner mSpinner,String mField,String mValue) {

        for (int i = 0; i < mSpinner.getCount(); i++) {
            Cursor value = (Cursor) mSpinner.getItemAtPosition(i);
            String mCurValue = value.getString(value.getColumnIndex(mField));

            //if (mCurValue == mValue) {
            if (mCurValue.equals(mValue)){
                mSpinner.setSelection(i);
                return;
            }
        }

    }






    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
            case R.id.expense_detail_menu_save_option:
                mSave();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.expense_detail_menu, menu);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check that it is the SecondActivity with an OK result
        if (requestCode == ALERT_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // get String data from the returning Intent
                String returnString = data.getStringExtra("RetValue");

                if (returnString.equals("1")) {
                    mDelete();
                }

            }
        }
    }



    private void ShowToast(String text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

    }

}
