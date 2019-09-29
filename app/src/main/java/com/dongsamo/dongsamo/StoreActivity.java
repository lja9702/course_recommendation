package com.dongsamo.dongsamo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.dongsamo.dongsamo.firebase_control.Store;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.functions.FirebaseFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.util.HashMap;
import java.util.Map;

public class StoreActivity extends AppCompatActivity {

    TextView store_text, store_info1, store_info2, store_info3, store_info4, store_info5, store_info6;
    TMapView tMapView;
    LinearLayout tmap_ln;
    private String apiKey = "b766d096-d3c5-4a56-b48f-d799ca065447";
    //진아: firebaseAnalytics 선언
    private FirebaseAnalytics mFirebaseAnalytics;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    ImageButton heart;
    private Intent intent;
    private long mLastClickTime = 0;

    float store_x=0, store_y=0;
    String store_name, store_food_menu, store_type, store_crtfc_gbn_nm, store_addr, store_call,store_unikey=null, user_id;
    JSONArray building_list;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("Parsing").child("Data").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                try {

                    Log.d("Test", String.valueOf(dataSnapshot.getValue()));
                    building_list = new JSONArray(String.valueOf(dataSnapshot.getValue()));
                    try {
                        intent = getIntent();
                        store_name = intent.getExtras().getString("store_name"); //가게명
                        store_x = intent.getExtras().getFloat("store_x",0);
                        store_y = intent.getExtras().getFloat("store_y", 0);
                        store_addr = intent.getExtras().getString("store_address", "-");
                        store_call = intent.getExtras().getString("store_call", "-");
                        store_type = intent.getExtras().getString("no_data", "-");
                        store_crtfc_gbn_nm = intent.getExtras().getString("no_data", "-");
                        store_food_menu = intent.getExtras().getString("no_data", "-");

                        if(store_x == 0 && store_y == 0)
                            insert_post();

                        store_text = (TextView)findViewById(R.id.store_name_textView);
                        store_info1 = (TextView)findViewById(R.id.store_info1);
                        store_info2 = (TextView)findViewById(R.id.store_info2);
                        store_info3 = (TextView)findViewById(R.id.store_info3);
                        store_info4 = (TextView)findViewById(R.id.store_info4);
                        store_info5 = (TextView)findViewById(R.id.store_info5);
                        store_info6 = (TextView)findViewById(R.id.store_info6);

                        heart = (ImageButton)findViewById(R.id.heart);
                        store_text.setText(store_name);

                        tmap_ln = (LinearLayout) findViewById(R.id.store_tmap);
                        tMapView = new TMapView(StoreActivity.this);
                        tMapView.setSKTMapApiKey(apiKey);
                        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);
                        tMapView.setZoomLevel(18);
                        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
                        tMapView.setCompassMode(false);
                        tMapView.setTrackingMode(true);
                        tmap_ln.addView(tMapView);

                        pinpinEE(store_name, store_x ,store_y, "TEST_"+store_name);

                        store_info1.setText("이름 : "+store_name);
                        store_info2.setText("도로명주소 : "+store_addr);
                        store_info3.setText("전화번호 : "+store_call);
                        store_info4.setText("업태 : "+store_type);
                        store_info5.setText("식품인증구분 : "+store_crtfc_gbn_nm);
                        store_info6.setText("대표메뉴 : "+store_food_menu);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

                Log.w("TAGs", "Failed to read value.", databaseError.toException());
            }
        });

        //firebaseAnalytics 초기화
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        databaseReference.child("Users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Member member = dataSnapshot.getValue(Member.class);
                Log.d("TAGS", member.getId());

                databaseReference.child("favorite").child(member.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot mdataSnapshot) {
                        boolean is_heart = false;
                        for (DataSnapshot snapshot : mdataSnapshot.getChildren()) {
                            if (snapshot.getValue().equals(store_unikey)) {
                                is_heart = true;
                            }
                        }

                        if (is_heart) {
                            heart.setImageResource(R.drawable.true_heart);
                        }
                        else {
                            heart.setImageResource(R.drawable.false_heart);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w("TAGS", "Failed to read value.", databaseError.toException());
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.w("TAGs", "Failed to read value.", databaseError.toException());
            }
        });
    }

    protected void pinpinEE(String pin_name, float x, float y, String pin_id){
        TMapPoint tpoint = new TMapPoint(y, x);

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

        tMapView.setLocationPoint(x,y);

        tMapView.addMarkerItem(pin_id, tItem);
    }

    public void onClick_heart(View view) throws InterruptedException {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
            return;
        }

        if(store_unikey == null || store_unikey.equals("-") || store_unikey.equals("")){
            Toast.makeText(getApplicationContext(), "공공데이터에 등록된 맛집만 좋아요를 누를 수 있습니다.", Toast.LENGTH_LONG).show();
            return;
        }

        mLastClickTime = SystemClock.elapsedRealtime();

        String contentType = store_type;

        Log.d("TAG", user_id+" "+store_unikey + " " + store_type);

        //function으로 보낼 데이터들

        Map<String, Object> data = new HashMap<>();
        data.put("user_id", user_id);
        data.put("store_id", store_unikey);

        databaseReference.child("Users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Member member = dataSnapshot.getValue(Member.class);
                Log.d("TAGS", member.getId());

                databaseReference.child("favorite").child(member.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot mdataSnapshot) {
                        boolean is_heart = false;
                        int last_cnt = 0;
                        int empty_cnt = 0;
                        int before_index = -1;
                        int if_index = -1;
                        int cnt = 0;
                        Log.d("TAGS", "datasnapshot : "+ mdataSnapshot.getValue());
                        for (DataSnapshot snapshot : mdataSnapshot.getChildren()) {
                            Log.d("TAGS", "snapshot key : " + snapshot.getKey());
                            Log.d("TAGS", "snapshot VALUE : " + snapshot.getValue());

                            int index = Integer.parseInt(snapshot.getKey());
                            if (before_index == index - 1) {
                                before_index = index;
                            }
                            else {
                                if (if_index == -1) {
                                    if_index = index - 1;
                                }
                            }

                            if (snapshot.getValue().equals(store_unikey)) {
                                is_heart = true;
                                cnt = Integer.parseInt(snapshot.getKey());
                            }
                            last_cnt++;
                            Log.d("TAGS", "favorite : " + snapshot.getKey());
                        }

                        if (is_heart) {
                            mdataSnapshot.getRef().child(String.valueOf(cnt)).removeValue();
                            heart.setImageResource(R.drawable.false_heart);
                            Log.d("TAGS", "in here");
                        }
                        else {
                            if (if_index == -1) {
                                mdataSnapshot.getRef().child(String.valueOf(last_cnt)).setValue(store_unikey);
                            }
                            else {
                                mdataSnapshot.getRef().child(String.valueOf(if_index)).setValue(store_unikey);
                            }
                            heart.setImageResource(R.drawable.true_heart);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w("TAGS", "Failed to read value.", databaseError.toException());
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.w("TAGs", "Failed to read value.", databaseError.toException());
            }
        });

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType);
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, user_id);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if( firebaseUser == null ){
            Intent intent = new Intent(StoreActivity.this, LoginActivity.class);
            startActivity(intent);
            StoreActivity.this.finish();
        }
        get_userID();
    }

    public void get_userID(){

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
// update name, ... TextView
        databaseReference.child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@android.support.annotation.NonNull DataSnapshot dataSnapshot) {
                Member member = dataSnapshot.getValue(Member.class);
                try {
                    user_id = member.getId();
                    Log.d("TAG", user_id);
                }
                catch (NullPointerException e){
                    Toast.makeText(StoreActivity.this, "에러로 인해 로그아웃되었습니다.", Toast.LENGTH_LONG);
                    firebaseAuth.signOut();

                    Intent intent = new Intent(StoreActivity.this, LoginActivity.class);
                    startActivity(intent);
                    StoreActivity.this.finish();
                }
            }

            @Override
            public void onCancelled(@android.support.annotation.NonNull DatabaseError databaseError) {

                Log.w("TAG", "Failed to read value.", databaseError.toException());
            }
        });
    }

    JSONObject JS = null;
    private void insert_post() throws Exception {
        //UPSO_NM: 가게명, CGG_CODE_NM: 자치구명, BIZCND_CODE_NM : 업태명, Y_DNTS : 지도 Y좌표, X_CNTS: 지도 X좌표, TEL_NO: 전화번호
        //RDN_CODE_NM: 도로명주소,
        String ps_unikey=null, ps_name = null, ps_type = null, ps_call= null, ps_address= null, ps_food_menu=null, ps_gbn = null;
        double ps_store_x = 0, ps_store_y = 0;

        for (int i = 0; i < building_list.length(); i++) {
            try {
                JS = building_list.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (JS != null) {
                ps_name = JS.optString("UPSO_NM");
                ps_call = JS.optString("TEL_NO");
                ps_address = JS.optString("RDN_CODE_NM") + JS.optString("RDN_DETAIL_ADDR");
                ps_type = JS.optString("BIZCND_CODE_NM");
                ps_unikey = JS.optString("CRTFC_UPSO_MGT_SNO");
                ps_store_x = JS.optDouble("X_CNTS");
                ps_store_y = JS.optDouble("Y_DNTS");
                ps_food_menu = JS.optString("FOOD_MENU");
                ps_gbn = JS.optString("CRTFC_GBN_NM");

                if (ps_name.equals(store_name)) {
                    Log.d("TAG", "Intent22: "+ps_name+"  "+ps_call+"  "+ps_address+"  "+ps_type+"  "+ps_unikey+"  "+ps_store_x+"  "+ps_store_y);
                    store_x = (float)ps_store_x;
                    store_y = (float)ps_store_y;
                    store_name = ps_name;

                    if(ps_type == null || ps_type.equals(""))
                        store_type = "-";
                    else
                        store_type = ps_type;

                    if(ps_address == null || ps_address.equals(""))
                        store_addr = "-";
                    else
                        store_addr = ps_address;

                    if(ps_call == null || ps_call.equals(""))
                        store_call = "-";
                    else
                        store_call = ps_call;

                    if(ps_unikey == null || ps_unikey.equals(""))
                        store_unikey = "-";
                    else
                        store_unikey = ps_unikey;

                    if(ps_gbn == null || ps_gbn.equals(""))
                        store_crtfc_gbn_nm = "-";
                    else
                        store_crtfc_gbn_nm = ps_gbn;

                    if(ps_food_menu == null || ps_food_menu.equals(""))
                        store_food_menu = "-";
                    else
                        store_food_menu = ps_food_menu;
                    return;
                }
            }
        }


    }
}
