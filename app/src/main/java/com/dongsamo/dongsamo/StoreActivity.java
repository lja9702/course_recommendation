package com.dongsamo.dongsamo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dongsamo.dongsamo.firebase_control.Store;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

public class StoreActivity extends AppCompatActivity {
    ImageView store_image;
    TextView store_text;
    TMapView tMapView;
    LinearLayout ln;
    private String apiKey = "c13a727c-011c-49bc-bb6c-a0c3ce5777f8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        store_image = (ImageView)findViewById(R.id.store_img);
        store_text = (TextView)findViewById(R.id.store_txt);
        store_text.setMovementMethod(new ScrollingMovementMethod());

        //kong todo 69 StoreRecyclerViewAdapter에서 인텐트로 보낸 store_name 정보 띄우기.
        StoreCard sc = (StoreCard) getIntent().getSerializableExtra("store");
        Glide.with(store_image).load(""+sc.getImg_url()).into(store_image);
        store_text.setText(sc.getName());

        ln = (LinearLayout)findViewById(R.id.tmap_store);
        tMapView = new TMapView(this);

        tMapView.setSKTMapApiKey(apiKey);
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);
        //tMapView.setIconVisibility(true); // 내 위치 (gps권한 주기)
        tMapView.setZoomLevel(15);
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setCompassMode(true);
        tMapView.setTrackingMode(true);
        ln.addView(tMapView);

        pinpinEE("쿠우쿠우",(float)37.480897 ,(float)126.951086, "TEST_SO");

    }

    protected void pinpinEE(String pin_name, float x, float y, String pin_id){
        TMapPoint tpoint = new TMapPoint(x, y);

        TMapMarkerItem tItem = new TMapMarkerItem();

        tItem.setTMapPoint(tpoint);
        tItem.setName(pin_name);
        tItem.setVisible(TMapMarkerItem.VISIBLE);

        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.icon);
        tItem.setIcon(bitmap);
        tItem.setCalloutTitle(pin_name);
        tItem.setCanShowCallout(true);
        tItem.setAutoCalloutVisible(true);


        // 핀모양으로 된 마커를 사용할 경우 마커 중심을 하단 핀 끝으로 설정.
        tItem.setPosition((float)0.5, (float)1.0);         // 마커의 중심점을 하단, 중앙으로 설정

        tMapView.setLocationPoint(y,x);

        tMapView.addMarkerItem(pin_id, tItem);
    }

    private void setup(){
        
    }

}
