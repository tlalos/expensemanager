package com.example.tlalos.myapplication;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tlalos.myapplication.classes.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ProgressActivity extends Activity {

    Button cmdClose;
    ProgressBar pb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        cmdClose= (Button) findViewById(R.id.cmdProgressClose);
        pb= (ProgressBar) findViewById(R.id.prgProgress_Prog);

        cmdClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event) {
        TextView txt;
        txt= (TextView) findViewById(R.id.lblProgressMessage);

        if (event.getMessageType()==0){
            txt.setText(event.getMessage());
        }
        else if (event.getMessageType()==100) {
            txt.setText(event.getMessage());

            cmdClose.setEnabled(true);
        }


        //Toast.makeText(this, "Hey, my message" + event.getMessage(), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);

    }



}
