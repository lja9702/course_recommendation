package com.dongsamo.dongsamo;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class AIRunningActivity extends AppCompatActivity {

    String[] st_list, result_list;
    int eat_count;
    String course;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airunning);

        Intent i = getIntent();
        course = i.getExtras().getString("new_course");
        eat_count = i.getExtras().getInt("eat_count");
        st_list = course.split("  ");
        result_list = new String[5];
        //st_list[1]이 첫 번째. 만약 맛집이 아니면 그냥 두기!

        Log.d("count", course+"  "+eat_count);

        Handler handler = new Handler(){
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                Intent intent = new Intent(AIRunningActivity.this, DecidingActivity.class);
                intent.putExtra("new_course", course);
                startActivity(intent);
                finish();
            }

        };
        //handler.sendEmptyMessageDelayed(0,3000);
    }
}
