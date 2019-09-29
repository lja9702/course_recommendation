package com.dongsamo.dongsamo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
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

import com.google.android.gms.tasks.Task;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

//import static com.google.firebase.internal.FirebaseAppHelper.getUid;

public class StoreListActivity extends AppCompatActivity {

    static Activity B_Activity;

    TextView address_textView;
    private TMapView tMapView;
    private String apiKey = "b766d096-d3c5-4a56-b48f-d799ca065447";
    double lon, lat;
    String now_location;
    String dong_location;

    String siteUrl = "http://openapi.seoul.go.kr:8088/";
    String ID = "6c5474475266627737325756715870";
    String contents = "/json/CrtfcUpsoInfo/1/1000/ / / / /";
    JSONArray building_list;

    String[] jsonName = {"CRTFC_UPSO_MGT_SNO", "UPSO_SNO", "UPSO_NM", "CGG_CODE", "CGG_CODE_NM", "COB_CODE_NM", "BIZCND_CODE_NM",
            "OWNER_NM", "CRTFC_GBN", "CRTFC_GBN_NM", "CRTFC_CHR_NM", "CRTFC_CHR_ID", "CRTFC_YMD", "USE_YN", "MAP_INDICT_YN", "CRTFC_CLASS",
            "Y_DNTS","X_CNTS", "TEL_NO", "RDN_DETAIL_ADDR", "RDN_ADDR_CODE", "RDN_CODE_NM", "BIZCND_CODE", "COB_CODE", "CRTFC_SNO",
            "CRT_TIME", "CRT_USR", "UPD_TIME", "FOOD_MENU", "GNT_NO", "CRTFC_YN"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);

        address_textView = (TextView)findViewById(R.id.address_textView);

        tMapView = (TMapView)findViewById(R.id.store_list_tmap);

        tMapView.setSKTMapApiKey(apiKey);
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);
        tMapView.setIconVisibility(true); // 내 위치 (gps권한 주기)
        tMapView.setZoomLevel(15);
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setCompassMode(false);
        tMapView.setTrackingMode(true);

        GPSclass gpSclass = new GPSclass(this);
        Location loc = gpSclass.getLocation();
        lon = loc.getLongitude();
        lat = loc.getLatitude();

        tMapView.setLocationPoint(lon,lat);

        //pinpinEE("테스트(내위치)", lat, lon, "test");

        String api_returns = stringToApi();
        //내 위치 기반으로 무슨 구 인지 찾아오기
        String store_num_find = api_returns.substring(api_returns.indexOf("\"name\""));
        String store_num_array[] = store_num_find.split("\"");
        now_location = store_num_array[75];
        dong_location = store_num_array[93];
        address_textView.setText(now_location+" "+dong_location);


        Log.d("tags", stringToApi());
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

    public String stringToApi(){
        ReGeocodeThreadClass test = new ReGeocodeThreadClass(lat, lon);
        Thread t = new Thread(test);
        t.start();
        while(test.get_result() == null);

        return test.get_result();
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

        tMapView.addMarkerItem(pin_id, tItem);

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

                Intent intent = new Intent(StoreListActivity.this, StoreActivity.class);
                intent.putExtra("store_name", tMapMarkerItem.getName());
                startActivity(intent);
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

            try {
                insert_post( );
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }
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
            re_apiURL = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?request=coordsToaddr&coords="+my_lon+","+my_lat+"&sourcecrs=epsg:4326&output=json&orders=legalcode,admcode&X-NCP-APIGW-API-KEY-ID=otvptwo8i7&X-NCP-APIGW-API-KEY=ea7s1S6H0iRWh1afXSjK2cNCgtPqtsJNuhK3FLFM";
            Log.d("tags", re_apiURL);
            //re_apiURL = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?request=coordsToaddr&coords="+"126.939512,37.512402"+"&sourcecrs=epsg:4326&output=json&orders=legalcode,admcode&X-NCP-APIGW-API-KEY-ID=otvptwo8i7&X-NCP-APIGW-API-KEY=ea7s1S6H0iRWh1afXSjK2cNCgtPqtsJNuhK3FLFM";
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