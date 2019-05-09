package com.donsamo.dongsamo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick_shop_list(View view){
        Intent intent = new Intent(MainActivity.this, CourseActivity.class);
        startActivity(intent);



        //Toast.makeText(MainActivity.super.getApplicationContext(), "주변 상점 리스트", Toast.LENGTH_LONG).show();
    }

    public void onClick_course_ask(View view){
        Intent intent = new Intent(MainActivity.this, CourseActivity.class);
        startActivity(intent);
    }

    //by kongil
    public void onClick_manager_mode(View view){
        Intent intent = new Intent(MainActivity.this, PostActivity.class);
        startActivity(intent);
    }

}
