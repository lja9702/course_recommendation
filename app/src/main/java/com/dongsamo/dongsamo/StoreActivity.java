package com.dongsamo.dongsamo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.dongsamo.dongsamo.firebase_control.Store;

public class StoreActivity extends AppCompatActivity {
    ImageView store_image;
    TextView store_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        store_image = (ImageView)findViewById(R.id.store_img);
        store_text = (TextView)findViewById(R.id.store_txt);
        store_text.setMovementMethod(new ScrollingMovementMethod());

        //kong todo 69 StoreRecyclerViewAdapter에서 인텐트로 보낸 store_name 정보 띄우기.
        //Store s = (Store) getIntent().getSerializableExtra("store");
//        Intent received = new Intent();
//        Store s = new Store(received.getStringExtra("store_name"), received.getStringExtra("store_img_url"),  received.getDoubleExtra("store_heart",0), received.getDoubleExtra("store_distance",0), received.getBooleanExtra("store_is_heart", false));

//        Log.d("TAG", s.getUrl());

//        store_image.setImageURI(Uri.parse(s.getUrl()));

        Log.d("TAG", getIntent().getStringExtra("store_img_url"));
        store_text.setText(getIntent().getStringExtra("store_name"));
        //store_text.setText(getIntent().getStringExtra(s.getName()));
    }

    private void setup(){
        
    }

}
