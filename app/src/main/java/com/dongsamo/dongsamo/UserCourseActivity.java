package com.dongsamo.dongsamo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class UserCourseActivity extends AppCompatActivity {

    ImageButton next_btn, fin_btn,user_course_finish_btn;
    TextView course_item;
    int count = 0, ori_count=0;
    String course="", new_course="", add_course = "";
    String[] store_list;

    String siteUrl = "http://openapi.seoul.go.kr:8088/";
    String ID = "6c5474475266627737325756715870";
    String contents = "/json/CrtfcUpsoInfo/1/1000/ / / / /";
    JSONArray building_list;

    String[] jsonName = {"CRTFC_UPSO_MGT_SNO", "UPSO_SNO", "UPSO_NM", "CGG_CODE", "CGG_CODE_NM", "COB_CODE_NM", "BIZCND_CODE_NM",
            "OWNER_NM", "CRTFC_GBN", "CRTFC_GBN_NM", "CRTFC_CHR_NM", "CRTFC_CHR_ID", "CRTFC_YMD", "USE_YN", "MAP_INDICT_YN", "CRTFC_CLASS",
            "Y_DNTS","X_CNTS", "TEL_NO", "RDN_DETAIL_ADDR", "RDN_ADDR_CODE", "RDN_CODE_NM", "BIZCND_CODE", "COB_CODE", "CRTFC_SNO",
            "CRT_TIME", "CRT_USR", "UPD_TIME", "FOOD_MENU", "GNT_NO", "CRTFC_YN"};

    private TMapView tMapView;
    private String apiKey = "b766d096-d3c5-4a56-b48f-d799ca065447";
    double lon, lat;
    String now_location, store="";
    float office_x=0, office_y=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_course);

        Intent intent = getIntent();

        ori_count = intent.getExtras().getInt("ori_count");
        count = intent.getExtras().getInt("count");
        store_list = intent.getExtras().getStringArray("store_list");
        now_location = intent.getExtras().getString("location","");
        new_course = intent.getExtras().getString("new_course", "");
        store = intent.getExtras().getString("store","");
        //**구청 받아와서 구청 중심으로 지도 보이기
        office_x = intent.getExtras().getFloat("office_x");
        office_y = intent.getExtras().getFloat("office_y");

        next_btn = (ImageButton)findViewById(R.id.user_course_next_btn);
        user_course_finish_btn = (ImageButton)findViewById(R.id.user_course_finish_btn);
        fin_btn = (ImageButton)findViewById(R.id.user_course_finish_btn);
        course_item = (TextView)findViewById(R.id.course_item);

        tMapView = (TMapView)findViewById(R.id.user_course_tmap);
        tMapView.setSKTMapApiKey(apiKey);
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);
        tMapView.setZoomLevel(13);
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setCompassMode(false);
        tMapView.setTrackingMode(true);

        Log.d("OFFICE", "X: "+office_x+" Y: "+office_y);

        tMapView.setLocationPoint((double)office_x,(double)office_y);

        course_item.setText("("+(ori_count-count+1)+"/"+ori_count+")"+" "+store);

        if(count == 1){
            next_btn.setVisibility(View.GONE);
            user_course_finish_btn.setVisibility(View.VISIBLE);
        }

        try {
            new Task().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void pinpinEE(final String pin_name, final double x, final double y, String pin_id){
        TMapPoint tpoint = new TMapPoint(y, x);

        TMapMarkerItem tItem = new TMapMarkerItem();

        tItem.setTMapPoint(tpoint);
        tItem.setName(pin_name);
        tItem.setVisible(TMapMarkerItem.VISIBLE);

        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.icon);
        tItem.setIcon(bitmap);
        tItem.setCalloutTitle(pin_name);
        tItem.setCanShowCallout(true);
        tItem.setAutoCalloutVisible(false);

        Bitmap bitmap2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.charc);
        int resizeWidth = 68;

        double aspectRatio = (double) bitmap2.getHeight() / (double) bitmap2.getWidth();
        int targetHeight = (int) (resizeWidth * aspectRatio);
        Bitmap result = Bitmap.createScaledBitmap(bitmap2, resizeWidth, targetHeight, false);
        if (result != bitmap2) {
            bitmap2.recycle();
        }

        tItem.setCalloutRightButtonImage(result);

        // 핀모양으로 된 마커를 사용할 경우 마커 중심을 하단 핀 끝으로 설정.
        tItem.setPosition((float)0.5, (float)0.5);         // 마커의 중심점을 하단, 중앙으로 설정

        tMapView.setLocationPoint(x,y);
        tMapView.addMarkerItem(pin_id, tItem);

        tMapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {
                JSONObject JS = null;

                String ps_name = null;

                for (int i = 0; i < building_list.length(); i++) {
                    try {
                        JS = building_list.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (JS != null) {
                        ps_name = JS.optString("UPSO_NM");

                        Log.d("Tag", "pinName: "+tMapMarkerItem.getName()+" ps_name: "+ps_name);
                        if(ps_name.equals(tMapMarkerItem.getName())) {
                            add_course = tMapMarkerItem.getName();
                            Log.d("Tag", "pinName: "+add_course);
                            course_item.setText("("+(ori_count-count+1)+"/"+ori_count+")"+" "+add_course);

                            break;
                        }
                    }
                }

            }
        });
    }

    JSONObject JS = null;
    private void insert_post() throws Exception {
        //UPSO_NM: 가게명, CGG_CODE_NM: 자치구명, BIZCND_CODE_NM : 업태명, Y_DNTS : 지도 Y좌표, X_CNTS: 지도 X좌표, TEL_NO: 전화번호
        //RDN_CODE_NM: 도로명주소,


        String ps_name = null, ps_type = null, ps_call= null, ps_address= null;
        double store_x = 0, store_y = 0;
        Log.d("LOG", "hi Log   "+building_list.length());

        int cnt = 0;
        for (int i = 0; i < building_list.length(); i++) {
            if(cnt > 30)
                break;
            cnt++;

            JS = building_list.getJSONObject(i);
            if (JS != null) {
                for (int j = 0; j < jsonName.length; j++) {
                    if (jsonName[j].equals("UPSO_NM")) {
                        ps_name = JS.optString("UPSO_NM");
                    } else if (jsonName[j].equals("TEL_NO")) {
                        ps_call = JS.optString("TEL_NO");
                    } else if (jsonName[j].equals("RDN_CODE_NM")) {
                        ps_address = JS.optString("RDN_CODE_NM");
                    } else if (jsonName[j].equals("BIZCND_CODE_NM")) {
                        ps_type = JS.optString("BIZCND_CODE_NM");
                    }  else if( jsonName[j].equals("X_CNTS")){
                        store_x = JS.optDouble("X_CNTS");
                    }
                    else if(jsonName[j].equals("Y_DNTS")){
                        store_y = JS.optDouble("Y_DNTS");
                    }
                }
            }

            if(ps_name != null) {
                Log.d("LOG", "hi "+ps_name+" "+store_x);
                pinpinEE(ps_name, store_x, store_y, "Pin"+ps_name);
            }
        }

    }

    public class Task extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            String result = null;
            try {
                url = new URL(""+siteUrl+ID+contents+now_location);

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

                    JSONObject jsonObject1 = new JSONObject(result);
                    String CrtfcUpsoInfo = jsonObject1.getString("CrtfcUpsoInfo");
                    JSONObject jsonObject2 = new JSONObject(CrtfcUpsoInfo);
                    String row = jsonObject2.getString("row");
                    building_list = new JSONArray(row);

                    reader.close();
                } else {
                    Log.i("통신 결과", conn.getResponseCode() + "에러");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                insert_post( );
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }
    }

    public void onClick_user_course_next_btn(View view){
        Intent intent = new Intent(UserCourseActivity.this, UserCourseActivity.class);
        new_course = new_course.concat("  "+add_course);
        Log.d("NEW_COURSE", "hihi  "+new_course);
        intent.putExtra("new_course", new_course);
        intent.putExtra("ori_count", ori_count);
        intent.putExtra("count", count-1);
        intent.putExtra("store_list", store_list);
        intent.putExtra("store", store_list[ori_count-count+2]);
        intent.putExtra("office_x", office_x);
        intent.putExtra("office_y", office_y);
        startActivity(intent);
        finish();
    }

    public void onClick_user_course_finish_btn(View view){
        Intent intent = new Intent(UserCourseActivity.this, DecidingActivity.class);
        new_course = new_course.concat("  "+add_course);
        Log.d("NEW_COURSE", "hihi  "+new_course);
        intent.putExtra("new_course", new_course);
        intent.putExtra("count", ori_count);

        startActivity(intent);
        finish();
    }


}
