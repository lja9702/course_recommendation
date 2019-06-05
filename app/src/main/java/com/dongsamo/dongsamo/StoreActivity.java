package com.dongsamo.dongsamo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

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
    }

    private void setup(){
        
    }

}
