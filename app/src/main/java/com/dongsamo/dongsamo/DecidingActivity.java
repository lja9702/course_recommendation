package com.dongsamo.dongsamo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class DecidingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deciding);
    }

    public void onClick_decide_course(View view){
        Toast.makeText(DecidingActivity.super.getApplicationContext(), "이 코스로 결정!", Toast.LENGTH_LONG).show();

    }

    public void onClick_no_course(View view){
        Toast.makeText(DecidingActivity.super.getApplicationContext(), "다른 코스 보여줘!", Toast.LENGTH_LONG).show();

    }
}
