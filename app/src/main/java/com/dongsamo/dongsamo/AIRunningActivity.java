package com.dongsamo.dongsamo;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AIRunningActivity extends AppCompatActivity {

    String[] st_list, result_list;
    int eat_count;
    String course;
    String user_id="hi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airunning);



        Intent i = getIntent();
        course = i.getExtras().getString("new_course");
        eat_count = i.getExtras().getInt("eat_count");
        user_id = i.getExtras().getString("user_id");
        st_list = course.split("  ");
        result_list = new String[5];
        //st_list[1]이 첫 번째. 만약 맛집이 아니면 그냥 두기!

        Log.d("count", course+"  "+eat_count+"  "+user_id);

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
