package com.dongsamo.dongsamo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class CourseActivity extends AppCompatActivity {

    TextView course_text;
    String textview_txt = null;
    static String course = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        course_text = (TextView)findViewById(R.id.course_text);
        course_text.setMovementMethod(new ScrollingMovementMethod());

        try{
            Intent intent = getIntent();
            String name = intent.getExtras().getString("store_name");

            course_text.setText(course + " " + name);
        }catch (NullPointerException e){

        }
        //course_text.setText(course);

    }

    public void onClick_direct_add_btn(View view){
        course = String.valueOf(course_text.getText());
        Intent intent = new Intent(CourseActivity.this, UserCourseActivity.class);
        intent.putExtra("textview", course);
        startActivity(intent);
        finish();
    }

    public void onClick_trash_btn(View view){
        course_text.setText("");
    }




    public void onClick_shop_btn(View view){
        switch (view.getId()){
            case R.id.restaurant_btn:
                textview_txt = String.valueOf(course_text.getText());
                course_text.setText(textview_txt+"  밥");
                break;
            case R.id.show_btn:
                textview_txt = String.valueOf(course_text.getText());
                course_text.setText(textview_txt+"  문화/공연");
                break;
            case R.id.cafe_btn:
                textview_txt = String.valueOf(course_text.getText());
                course_text.setText(textview_txt+"  카페");
                break;
            case R.id.etc_btn:
                Intent intent = new Intent(this, DirectAddActivity.class);
                //todo

                startActivity(intent);
//                textview_txt = String.valueOf(course_text.getText());
//                course_text.setText(textview_txt+"  원하는 장소");
                break;
        }
    }

    public void onClick_ai_ask_btn(View view){
        Intent intent = new Intent(CourseActivity.this, AIRunningActivity.class);
        startActivity(intent);
    }

    public void onClick_course_location_btn(View view){//밥플 지역

    }
}
