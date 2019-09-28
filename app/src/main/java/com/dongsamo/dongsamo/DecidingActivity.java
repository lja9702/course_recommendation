package com.dongsamo.dongsamo;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.MotionEvent;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
import java.util.concurrent.ExecutionException;


public class DecidingActivity extends AppCompatActivity {


    private LinearLayout activity_deciding, bottom_layout;
    private TMapView tMapView;
    private String apiKey = "b766d096-d3c5-4a56-b48f-d799ca065447";
    private ImageButton decide_course_btn;
    private ImageButton no_course_btn;
    TMapData tmapdata = new TMapData();
    String course="";
    String[] store_list;
    String store_name, result;
    ArrayList passList;
    double store_x=0, store_y=0;
    TMapPoint tp1 = null, tp2= null;
    String siteUrl = "http://openapi.seoul.go.kr:8088/";
    String ID = "6c5474475266627737325756715870";
    String contents = "/json/CrtfcUpsoInfo/1/1000/";
    JSONArray building_list;
    String now_location, office, ps_name = null;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deciding);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = firebaseAuth.getCurrentUser();


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

        Intent intent = getIntent();
        course = intent.getExtras().getString("new_course","");
        course = course.substring(2);
        final String recom_store = intent.getExtras().getString("recom_store");

        office = intent.getExtras().getString("office");
        office = office.substring(0, office.length()-1);

        Log.d("Course222", "full:"+office+"  "+course);

        databaseReference.child("Users").child(firebaseUser.getUid()).child("Recom_Store").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue();
                dataSnapshot.getRef().setValue(recom_store);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("TAG", "Failed to read value.", databaseError.toException());
            }
        });

        store_list = course.split("  ");
        passList = new ArrayList<TMapPoint>();

        //course = "  "+course;
        ///출발지
        try {
            Log.d("Course222", "first:"+store_list[1]);
            store_x = store_y = 0;
            store_name = store_list[1];
            new FoodTask().execute().get();
            insert_post(result);
            if(store_x == store_y && store_x == 0){
                String api_returns = stringToApi(store_list[1]);
                Log.d("Course222", "log:"+api_returns);
                String now_data = api_returns.substring(api_returns.indexOf("\"x\""));
                String now_data_array[] = now_data.split("\"");
                store_x =  Float.parseFloat(now_data_array[3]);
                store_y =  Float.parseFloat(now_data_array[7]);
            }

            tMapView.setLocationPoint(store_x,store_y);
            tp1 = new TMapPoint(store_y, store_x);
            Log.d("Course222", "first:"+store_x+" "+store_y);
            pinpinEE(store_list[1], store_x, store_y,"no"+store_list[1]+"_test");

            store_x = store_y = 0;
            ///시작지
            Log.d("Course222", "second:"+store_list[2]);
            store_name = store_list[2];
            insert_post(result);
            if(store_x == store_y && store_x == 0){
                String api_returns = stringToApi(store_list[2]);
                Log.d("Course222", "log:"+api_returns);
                String now_data = api_returns.substring(api_returns.indexOf("\"x\""));
                String now_data_array[] = now_data.split("\"");
                store_x =  Float.parseFloat(now_data_array[3]);
                store_y =  Float.parseFloat(now_data_array[7]);
            }
            Log.d("Course222", "second:"+store_x+" "+store_y);
            tp2 = new TMapPoint(store_y, store_x);
            pinpinEE(store_list[2], store_x, store_y,"no"+store_list[2]+"_test");

            for(int i=3; i < store_list.length; i++){
                store_x = store_y = 0;
                store_name = store_list[i];
                insert_post(result);
                if(store_x == store_y && store_x == 0){
                    String api_returns = stringToApi(store_list[i]);
                    Log.d("Course222", "log:"+api_returns);
                    String now_data = api_returns.substring(api_returns.indexOf("\"x\""));
                    String now_data_array[] = now_data.split("\"");
                    store_x =  Float.parseFloat(now_data_array[3]);
                    store_y =  Float.parseFloat(now_data_array[7]);
                }
                pinpinEE(store_list[i],store_x,store_y,"a"+store_list[i]+"_test");
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(int i=0; i<passList.size(); i++){
            Log.d("passlist", String.valueOf(((TMapPoint) passList.get(i)).getLatitude()));
        }

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

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if( firebaseUser == null ){
            Intent intent = new Intent(DecidingActivity.this, LoginActivity.class);
            startActivity(intent);
            DecidingActivity.this.finish();
        }
    }

    public String stringToApi(String store_name){
        GeocodeThreadClass test = new GeocodeThreadClass(store_name);
        Thread t = new Thread(test);
        t.start();
        while(test.get_result() == null);
        return test.get_result();
    }

    protected void pinpinEE(String pin_name, double x, double y, String pin_id){
        TMapPoint tpoint = new TMapPoint(store_y, store_x);
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

        Bitmap bitmap2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.charc);
        int resizeWidth = 68;

        double aspectRatio = (double) bitmap2.getHeight() / (double) bitmap2.getWidth();
        int targetHeight = (int) (resizeWidth * aspectRatio);
        Bitmap result = Bitmap.createScaledBitmap(bitmap2, resizeWidth, targetHeight, false);
        if (result != bitmap2) {
            bitmap2.recycle();
        }

        tItem.setCalloutRightButtonImage(result);

        tMapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {
                JSONObject JS = null;

                String ps_unikey=null, ps_name = null, ps_type = null, ps_call= null, ps_address= null;
                double store_x = 0, store_y = 0;

                for (int i = 0; i < building_list.length(); i++) {
                    try {
                        JS = building_list.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (JS != null) {
                        ps_name = JS.optString("UPSO_NM");
                        ps_call = JS.optString("TEL_NO");
                        ps_address = JS.optString("RDN_CODE_NM")+JS.optString("RDN_DETAIL_ADDR");
                        ps_type = JS.optString("BIZCND_CODE_NM");
                        ps_unikey = JS.optString("CRTFC_UPSO_MGT_SNO");
                        store_x = JS.optDouble("X_CNTS");
                        store_y = JS.optDouble("Y_DNTS");

                        Log.d("Tag", "pinName: "+tMapMarkerItem.getName()+" ps_name: "+ps_name);
                        if(ps_name.equals(tMapMarkerItem.getName())) {
                            break;
                        }
                    }
                }

                Intent intent = new Intent(DecidingActivity.this, StoreActivity.class);
                intent.putExtra("store_unikey", ps_unikey);
                intent.putExtra("store_name", tMapMarkerItem.getName());
                intent.putExtra("store_x", (float)store_x);
                intent.putExtra("store_y", (float)store_y);
                intent.putExtra("store_type", ps_type);
                intent.putExtra("store_call", ps_call);
                intent.putExtra("store_address", ps_address);

                startActivity(intent);
            }
        });

        // 핀모양으로 된 마커를 사용할 경우 마커 중심을 하단 핀 끝으로 설정.
        tItem.setPosition((float)0.5, (float)1.0);         // 마커의 중심점을 하단, 중앙으로 설정

        //

        tMapView.addMarkerItem(pin_id, tItem);
    }

    JSONObject JS = null;
    private boolean insert_post(String result) throws Exception {
        //UPSO_NM: 가게명, CGG_CODE_NM: 자치구명, BIZCND_CODE_NM : 업태명, Y_DNTS : 지도 Y좌표, X_CNTS: 지도 X좌표, TEL_NO: 전화번호
        //RDN_CODE_NM: 도로명주소,
        for (int i = 0; i < building_list.length(); i++) {
            JS = building_list.getJSONObject(i);

            if (JS != null) {
                ps_name = JS.optString("UPSO_NM");
                Log.d("Store", "업소: "+ps_name);
                if(ps_name != null) {
                    Log.d("Store", result+"  "+ps_name);
                    if(store_name.equals(ps_name)) {
                        Log.d("Success", "HI success");

                        store_x = JS.optDouble("X_CNTS");
                        store_y = JS.optDouble("Y_DNTS");

                        return true;
                    }
                }
            }
        }
        return false;
    }

    public class FoodTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            String result = null;
            try {
                url = new URL(""+siteUrl+ID+contents);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    String str = null;
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    result = buffer.toString();

                    Log.d("LOG", result);
                    JSONObject jsonObject1 = new JSONObject(result);
                    String CrtfcUpsoInfo = jsonObject1.getString("CrtfcUpsoInfo");
                    Log.d("LOG", "CrtfcUpsoInfo: " + CrtfcUpsoInfo);
                    JSONObject jsonObject2 = new JSONObject(CrtfcUpsoInfo);
                    String row = jsonObject2.getString("row");
                    Log.d("Log", "row: "+row);
                    building_list = new JSONArray(row);

                    reader.close();
                } else {
                    Log.i("통신 결과", conn.getResponseCode() + "에러");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            return result;
        }
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