package com.dongsamo.dongsamo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapMarkerItem2;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//import static com.google.firebase.internal.FirebaseAppHelper.getUid;

public class StoreListActivity extends AppCompatActivity {

    static Activity B_Activity;

    private TMapView tMapView;
    private String apiKey = "b766d096-d3c5-4a56-b48f-d799ca065447";
    double lon, lat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);

        tMapView = (TMapView)findViewById(R.id.store_list_tmap);

        tMapView.setSKTMapApiKey(apiKey);
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);
        //tMapView.setIconVisibility(true); // 내 위치 (gps권한 주기)
        tMapView.setZoomLevel(15);
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setCompassMode(true);
        tMapView.setTrackingMode(true);

        GPSclass gpSclass = new GPSclass(this);
        Location loc = gpSclass.getLocation();
        lon = loc.getLongitude();
        lat = loc.getLatitude();

        stringToApi();

        pinpinEE("테스트(내위치)", lat, lon, "test");

    }

    public String stringToApi(){
        ReGeocodeThreadClass test = new ReGeocodeThreadClass(lat, lon);
        Thread t = new Thread(test);
        t.start();
        while(test.get_result() == null);

        return test.get_result();
    }

    protected void pinpinEE(final String pin_name, final double x, final double y, String pin_id){
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

        tMapView.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {
            @Override
            public boolean onPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
                return false;
            }

            @Override
            public boolean onPressUpEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
                Intent intent = new Intent(StoreListActivity.this, StoreActivity.class);
                intent.putExtra("store_name", pin_name);
                intent.putExtra("store_x", x);
                intent.putExtra("store_y", y);
                startActivity(intent);
                return false;
            }
        });
    }

}

class ReGeocodeThreadClass implements Runnable {

    private String result = null;
    private String target_name;
    private double my_lat, my_lon;


    public ReGeocodeThreadClass(double x, double y) {
        this.my_lat = x;
        this.my_lon = y;
    }

    @Override
    public void run() {

        try {
            String re_apiURL = null;
            //String apiURL = "https://openapi.naver.com/v1/search/local?query=" + text + "&display=" + display + "&";
            re_apiURL = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?request=coordsToaddr&coords="+my_lat+","+my_lon+"&sourcecrs=epsg:4326&output=json&orders=legalcode,admcode";
            // coordinate : 검색할 중심 좌표, 서울이면 서울에서 검색, 부산이면 부산에서 검색
            URL re_url = new URL(re_apiURL);
            HttpURLConnection con = (HttpURLConnection) re_url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }

            br.close();
            con.disconnect();
            Log.d("tags", sb.toString());
            this.result = sb.toString();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String get_result() {
        return this.result;
    }
}