package com.example.tlalos.myapplication.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tlalos.myapplication.R;

public class AlertActivity extends Activity {

    Button cmdYes;
    Button cmdNo;
    TextView lblMess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        this.setTitle("Message");


        cmdYes=findViewById(R.id.cmdAlertYes);
        cmdNo=findViewById(R.id.cmdAlertNo);
        lblMess=findViewById(R.id.lblAlertMessage);


        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            lblMess.setText(extras.getString("Message")) ;

        }


        cmdYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.putExtra("RetValue", "1");
                setResult(RESULT_OK, intent);
                finish();

            }
        });

        cmdNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("RetValue", "0");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
