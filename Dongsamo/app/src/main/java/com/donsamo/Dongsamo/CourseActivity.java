package com.donsamo.Dongsamo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

public class CourseActivity extends AppCompatActivity {

    TextView course_text;
    String course = null;
    String textview_txt = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        course_text = (TextView)findViewById(R.id.course_text);
        course_text.setMovementMethod(new ScrollingMovementMethod());
    }

    public void onClick_direct_add(View view){
        Intent intent = new Intent(CourseActivity.this, DirectAddActivity.class);
        startActivity(intent);
    }

    public void onClick_trash(View view){
        course_text.setText("");
    }

    public void onClick_shop_btn(View view){
        switch (view.getId()){
            case R.id.restaurant_btn:
                textview_txt = String.valueOf(course_text.getText());
                course_text.setText(textview_txt+"  밥");
                break;
            case R.id.play_btn:
                textview_txt = String.valueOf(course_text.getText());
                course_text.setText(textview_txt+"  놀거리");
                break;
            case R.id.cafe_btn:
                textview_txt = String.valueOf(course_text.getText());
                course_text.setText(textview_txt+"  카페");
                break;
            case R.id.fresh_btn:
                textview_txt = String.valueOf(course_text.getText());
                course_text.setText(textview_txt+"  술");
                break;
        }
    }

    public void onClick_ai_ask(View view){
        Intent intent = new Intent(CourseActivity.this, AIRunningActivity.class);
        startActivity(intent);
    }

}
