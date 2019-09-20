package com.dongsamo.dongsamo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class UserCourseActivity extends AppCompatActivity {

    ImageButton next_btn, fin_btn;
    TextView course_item;
    int count = 0;
    String course="";
    String[] list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_course);

        Intent intent = getIntent();
        count = intent.getExtras().getInt("count");
        course = intent.getExtras().getString("course");

        list = course.split(" ");

        next_btn = (ImageButton)findViewById(R.id.user_course_next_btn);
        fin_btn = (ImageButton)findViewById(R.id.user_course_finish_btn);
        course_item = (TextView)findViewById(R.id.course_item);

        Log.d("tags", "list: "+list);
        course_item.setText(""+list[1]);

    }

    public void onClick_user_course_next_btn(View view){

    }

}
