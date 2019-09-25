package com.dongsamo.dongsamo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.location.Location;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.w3c.dom.Text;

import java.io.IOException;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.lang.String;

import javax.xml.parsers.ParserConfigurationException;

import static java.lang.Boolean.FALSE;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class DecidingActivity extends AppCompatActivity {


    private LinearLayout activity_deciding, bottom_layout;
    private TMapView tMapView;
    private String apiKey = "b766d096-d3c5-4a56-b48f-d799ca065447";
    private ImageButton decide_course_btn;
    private ImageButton no_course_btn;
    TMapData tmapdata = new TMapData();
    String course="";
    String[] store_list;
    String store_name;
    ArrayList passList;
    float store_x=0, store_y=0;
    TMapPoint tp1 = null, tp2= null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deciding);

        activity_deciding = (LinearLayout)findViewById(R.id.tmap);
        bottom_layout = (LinearLayout)findViewById(R.id.bottom_layout);
        tMapView = new TMapView(this);

        tMapView.setSKTMapApiKey(apiKey);
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);
        //tMapView.setIconVisibility(true); // 내 위치 (gps권한 주기)
        tMapView.setZoomLevel(15);
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setCompassMode(false);
        tMapView.setTrackingMode(true);
        activity_deciding.addView(tMapView);

//        no_course_btn = (ImageButton)findViewById(R.id.no_course_btn);
//        decide_course_btn = (ImageButton)findViewById(R.id.decide_course_btn);

        Intent intent = getIntent();
        course = intent.getExtras().getString("new_course","");
        Log.d("Course222", course);
        store_list = course.split("  ");
        passList = new ArrayList<TMapPoint>();

        //course = "  "+course;
        Log.d("Course222", store_list[1]);
        ///출발지
        String api_returns = stringToApi(store_list[2]);
        String now_data = api_returns.substring(api_returns.indexOf("\"x\""));
        String now_data_array[] = now_data.split("\"");
        store_x =  Float.parseFloat(now_data_array[3]);
        store_y =  Float.parseFloat(now_data_array[7]);
        tp1 = new TMapPoint(store_y, store_x);
        pinpinEE(store_list[2], store_y, store_x,"no"+store_list[2]+"_test");

        ///시작지
        String api_returns2 = stringToApi(store_list[3]);
        String now_data2 = api_returns2.substring(api_returns2.indexOf("\"x\""));
        String now_data_array2[] = now_data2.split("\"");
        store_x =  Float.parseFloat(now_data_array2[3]);
        store_y =  Float.parseFloat(now_data_array2[7]);
        tp2 = new TMapPoint(store_y, store_x);
        pinpinEE(store_list[3], store_y, store_x,"no"+store_list[3]+"_test");


        for(int i=4; i < store_list.length; i++){
            String api_returns3 = stringToApi(store_list[i]);
            String now_data3 = api_returns3.substring(api_returns3.indexOf("\"x\""));
            String now_data_array3[] = now_data3.split("\"");
            store_x =  Float.parseFloat(now_data_array3[3]);
            store_y =  Float.parseFloat(now_data_array3[7]);
            pinpinEE(store_list[i],store_y,store_x,"a"+store_list[i]+"_test");
        }


        for(int i=0; i<passList.size(); i++){
            Log.d("passlist", String.valueOf(((TMapPoint) passList.get(i)).getLatitude()));
        }

        // 경로 추가 예제
//        passList.add(point3);
//        passList.add(point4);

        tmapdata.findPathDataWithType(
                TMapData.TMapPathType.PEDESTRIAN_PATH,
                tp1, tp2, passList,
                0, new TMapData.FindPathDataListenerCallback() {

            @Override
            public void onFindPathData(TMapPolyLine polyLine) {
                polyLine.setLineColor(Color.RED);
                tMapView.addTMapPolyLine("Course_line" , polyLine);
            }
        });

    }

    public String stringToApi(String store_name){
        GeocodeThreadClass test = new GeocodeThreadClass(store_name);
        Thread t = new Thread(test);
        t.start();
        while(test.get_result() == null);
        return test.get_result();
    }

//
//    public void onClick_decide_course_btn(View view){
//        // Toast.makeText(DecidingActivity.super.getApplicationContext(), "이 코스로 결정!", Toast.LENGTH_LONG).show();
//        decide_course_btn.setVisibility(View.INVISIBLE);
//    }
//
//    public void onClick_no_course_btn(View view){
//        Intent intent = new Intent(DecidingActivity.this, AIRunningActivity.class);
//        startActivity(intent);
//        finish();
//    }

    protected void pinpinEE(String pin_name, float x, float y, String pin_id){
        TMapPoint tpoint = new TMapPoint(store_y, store_x);
        Log.d("pin_id", String.valueOf(pin_id.charAt(0)));
        if(pin_id.charAt(0) == 'a')
            passList.add(tpoint);

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
}


class GeocodeThreadClass implements Runnable{

    private String result = null;
    private String target_name;
    private float office_x, office_y;

    public GeocodeThreadClass(String target_name, float x, float y){
        this.target_name = target_name;
        this.office_x = x;
        this.office_y = y;
    }

    public GeocodeThreadClass(String target_name){
        this.target_name = target_name;
        this.office_x = 0;
        this.office_y = 0;
    }


    @Override
    public void run() {
        try {
            String apiURL=null;
            //String apiURL = "https://openapi.naver.com/v1/search/local?query=" + text + "&display=" + display + "&";
            if(this.office_x == 0 && this.office_y == 0)
                apiURL = "https://naveropenapi.apigw.ntruss.com/map-place/v1/search?query="+ this.target_name + "&coordinate=127.1054328,37.3595963&X-NCP-APIGW-API-KEY-ID=otvptwo8i7&X-NCP-APIGW-API-KEY=ea7s1S6H0iRWh1afXSjK2cNCgtPqtsJNuhK3FLFM";
            else
                apiURL = "https://naveropenapi.apigw.ntruss.com/map-place/v1/search?query="+ this.target_name + "&coordinate="+office_x+","+office_y+"&X-NCP-APIGW-API-KEY-ID=otvptwo8i7&X-NCP-APIGW-API-KEY=ea7s1S6H0iRWh1afXSjK2cNCgtPqtsJNuhK3FLFM";

            // coordinate : 검색할 중심 좌표, 서울이면 서울에서 검색, 부산이면 부산에서 검색
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
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
            this.result = sb.toString();

        } catch (Exception e) {
            System.out.println(e);
        }
    }


    public String get_result(){
        return this.result;
    }
}