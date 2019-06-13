package com.dongsamo.dongsamo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
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


public class DecidingActivity extends AppCompatActivity {


    private RelativeLayout activity_deciding;
    private TMapView tMapView;
    private Geocoder geocoder; //지오코더 -> 좌표에서 장소를, 장소에서 좌표를 알 수 있음
    private String apiKey = "c13a727c-011c-49bc-bb6c-a0c3ce5777f8";


    TMapData tmapdata = new TMapData();


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


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deciding);

        activity_deciding = new RelativeLayout(this);;
        tMapView = new TMapView(this);

        tMapView.setSKTMapApiKey(apiKey);
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);
        //tMapView.setIconVisibility(true); // 내 위치 (gps권한 주기)
        tMapView.setZoomLevel(15);
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setCompassMode(true);
        tMapView.setTrackingMode(true);
        activity_deciding.addView(tMapView);
        setContentView(activity_deciding);

      /*  TextView now_text_view = (TextView) this.findViewById(R.id.course_text);
        String target = now_text_view.getText().toString();
        String tar_arr[] = target.split(" ");
        int len = tar_arr.length;
        if(len == 0){
            // course_text 가 비었을 때

        }
        for(int i=0 ; i<len ; i++){
            String now_str = tar_arr[i];
            //DB불러오기 잘 모르겠삼..
            //pinpinEE(이름 위치...);
        }
*/

        pinpinEE("쿠우쿠우",(float)37.480897 ,(float)126.951086, "TEST_SO");
        pinpinEE("진이집",(float)37.475014, (float)126.953481,"TEST_JIN");
        pinpinEE("진이알바",(float)37.477777, (float)126.952437,"TEST_DON");
        pinpinEE("서울대입구역",(float)37.481209, (float)126.952713,"TEST_YUK");



        TMapPoint point1 = new TMapPoint((float)37.480897 ,(float)126.951086);
        TMapPoint point2 = new TMapPoint((float)37.475014, (float)126.953481);
        TMapPoint point3 = new TMapPoint((float)37.477777, (float)126.952437);
        TMapPoint point4 = new TMapPoint((float)37.481209, (float)126.952713);


        ArrayList passList = new ArrayList<TMapPoint>();
        TMapPolyLine tMapPolyLine = null;
        // 경로 추가 예제
        passList.add(point3);
        passList.add(point4);

        tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, point1, point2, passList, 0, new TMapData.FindPathDataListenerCallback() {
                    @Override
                    public void onFindPathData(TMapPolyLine polyLine) {
                        polyLine.setLineColor(Color.RED);
                        tMapView.addTMapPolyLine("TEST_LINE" , polyLine);
                    }
                });


    }

    public void onClick_decide_course(View view){
        Toast.makeText(DecidingActivity.super.getApplicationContext(), "이 코스로 결정!", Toast.LENGTH_LONG).show();

    }

    public void onClick_no_course(View view){
        Toast.makeText(DecidingActivity.super.getApplicationContext(), "다른 코스 보여줘!", Toast.LENGTH_LONG).show();

    }
}
