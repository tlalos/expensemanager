package com.example.tlalos.myapplication.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tlalos.myapplication.Data.DBHelper;
import com.example.tlalos.myapplication.R;

public class ExpenseTypeCartActivity extends AppCompatActivity {

    private static final int ALERT_ACTIVITY_REQUEST_CODE = 0;


    Button cmdDelete;
    EditText txtCodeId;
    EditText txtDescr;

    SQLiteDatabase db=null;
    DBHelper dbHelper=null;

    long recId=0;
    private int editMode=0;
    Context mContext;


    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_type_cart);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Expense Type");

        mContext=getApplicationContext();

        cmdDelete=(Button) findViewById(R.id.cmdDeleteExpenseType);
        txtCodeId=(EditText) findViewById(R.id.txtExpenseTypeCodeId);
        txtDescr=(EditText) findViewById(R.id.txtExpenseTypeDescr);

        dbHelper=new DBHelper(getApplicationContext());
        db=dbHelper.getReadableDatabase();

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            editMode=extras.getInt("EditMode") ;
            if (editMode==1){
                recId=extras.getLong("RecId") ;
                //ShowToast(Long.toString(recId));
            }

        }


        //edit mode stuff
        if(editMode==0){ //new
            mLoadNew();
        }
        else { //edit
            mLoad();
        }



        cmdDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //OLD WAY WITH INTENT
                //Intent intent = new Intent(mContext, AlertActivity.class);
                //intent.putExtra("Message","Delete Expense Type ?");
                //startActivityForResult(intent, ALERT_ACTIVITY_REQUEST_CODE);


                dialogBuilder=new AlertDialog.Builder(v.getContext());
                View view= getLayoutInflater().inflate(R.layout.activity_alert,null);


                Button cmdAlertYes=(Button) view.findViewById(R.id.cmdAlertYes);
                Button cmdAlertNo=(Button) view.findViewById(R.id.cmdAlertNo);
                TextView txtAlertMessage=(TextView) view.findViewById(R.id.lblAlertMessage);

                txtAlertMessage.setText("Delete Expense Type ?");


                dialogBuilder.setView(view);
                dialog=dialogBuilder.create();
                dialog.show();


                cmdAlertYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDelete();
                        dialog.dismiss();
                    }
                });

                cmdAlertNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


            }
        });


    }


    void mSave() {

        try {


            String mSQL="";
            if (editMode==1){//edit mode
                mSQL="update expensetype "+
                        "set "+
                        "codeid='"+txtCodeId.getText()+"',"+
                        "descr='"+txtDescr.getText()+"' "+
                        "where _id="+recId;
            }
            else // new mode
            {

                mSQL="insert into expensetype " +
                        "(codeid,descr) "+
                        "values "+
                        "("+
                        "'"+txtCodeId.getText()+"'," +
                        "'"+txtDescr.getText()+"' " +
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


    void mLoad() {

        //ShowToast(Long.toString(recId));

        String mSQL="SELECT "+
                "e._id,"+
                "coalesce(e.codeid,0) as codeid,"+
                "coalesce(e.descr,'') as descr "+
                "from expensetype e "+
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

            txtCodeId.setText(Integer.toString(c.getInt(c.getColumnIndexOrThrow("codeid"))));
            txtDescr.setText(c.getString(c.getColumnIndexOrThrow("descr")));
        }

    }


    void mLoadNew() {

        int maxCodeId=dbHelper.TableFieldMaxValue("expensetype","codeid");
        maxCodeId++;
        txtCodeId.setText(Integer.toString(maxCodeId));

        cmdDelete.setVisibility(View.INVISIBLE);

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


    void mDelete() {
        //db.execSQL("delete from expensetype where _id="+recId);
        db.execSQL("update expensetype set deleted=1 where _id="+recId);

        ShowToast("Expense Type deleted successfully");
        Intent intent = new Intent();
        intent.putExtra("keyName", "TEST RESULT DATA");
        setResult(RESULT_OK, intent);
        finish();

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
            case R.id.expensetype_detail_menu_save_option:
                mSave();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.expensetype_detail_menu, menu);
        return true;
    }


    private void ShowToast(String text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

    }


}
