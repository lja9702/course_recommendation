package com.dongsamo.dongsamo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class HelpActivity extends AppCompatActivity {

    boolean flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Intent intent = getIntent();
        flag = intent.getBooleanExtra("flag", true);

    }

    public void onClick_help_fin(View view){
        if(flag){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        finish();
    }


}
