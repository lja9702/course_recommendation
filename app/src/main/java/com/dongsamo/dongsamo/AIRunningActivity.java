package com.dongsamo.dongsamo;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class AIRunningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airunning);

        Intent i = getIntent();
        final String course = i.getExtras().getString("new_course");
        Handler handler = new Handler(){
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                Intent intent = new Intent(AIRunningActivity.this, DecidingActivity.class);
                intent.putExtra("new_course", course);
                startActivity(intent);
                finish();
            }

        };
        handler.sendEmptyMessageDelayed(0,3000);
    }
}
