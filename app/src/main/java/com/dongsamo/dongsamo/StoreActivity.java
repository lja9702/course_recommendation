package com.dongsamo.dongsamo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dongsamo.dongsamo.firebase_control.Store;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import com.google.firebase.analytics.FirebaseAnalytics;

public class StoreActivity extends AppCompatActivity {

    ImageView store_image;
    TextView store_text;
    TMapView tMapView;
    LinearLayout ln;
    private String apiKey = "b766d096-d3c5-4a56-b48f-d799ca065447";
    //진아: firebaseAnalytics 선언
    private FirebaseAnalytics mFirebaseAnalytics;
    double store_x, store_y;
    String store_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        Intent intent = getIntent();
        store_x = intent.getExtras().getDouble("store_x", 0);
        store_y = intent.getExtras().getDouble("store_y", 0);
        store_name = intent.getExtras().getString("store_name");

        store_text = (TextView)findViewById(R.id.store_name_textView);

        store_text.setText(store_name);
        tMapView = (TMapView)findViewById(R.id.store_tmap);
        tMapView.setSKTMapApiKey(apiKey);
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);
        tMapView.setZoomLevel(15);
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setCompassMode(true);
        tMapView.setTrackingMode(true);

        //firebaseAnalytics 초기화
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        pinpinEE(store_name, store_x ,store_y, "TEST_"+store_name);

    }

    protected void pinpinEE(String pin_name, double x, double y, String pin_id){
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
        tItem.setPosition((float)0.5, (float)0.5);         // 마커의 중심점을 하단, 중앙으로 설정

        tMapView.setLocationPoint(y,x);

        tMapView.addMarkerItem(pin_id, tItem);
    }

    private void setup(){

    }

    public void onClick_heart(View view){
        String contentType = "korean"; //업종
        String itemId = "1"; //가게 이름
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType);
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, itemId);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

}
