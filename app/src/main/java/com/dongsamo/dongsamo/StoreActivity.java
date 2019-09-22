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

import java.util.HashMap;
import java.util.Map;

public class StoreActivity extends AppCompatActivity {

    TextView store_text, store_info;
    TMapView tMapView;
    LinearLayout tmap_ln;
    private String apiKey = "b766d096-d3c5-4a56-b48f-d799ca065447";
    //진아: firebaseAnalytics 선언
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseFunctions mFunctions;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    ImageButton heart;
    private Intent intent;

    float store_x, store_y;
    String store_name, store_type, store_addr, store_call,store_unikey, user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        intent = getIntent();
        store_x = intent.getExtras().getFloat("store_x", 0);
        store_y = intent.getExtras().getFloat("store_y", 0);
        store_name = intent.getExtras().getString("store_name"); //가게명
        store_type = intent.getExtras().getString("store_type"); //가게업태
        store_addr = intent.getExtras().getString("store_address"); //가게 도로명 주소
        store_call = intent.getExtras().getString("store_call"); //전화번호
        store_unikey = intent.getExtras().getString("store_unikey"); //가게id

        store_text = (TextView)findViewById(R.id.store_name_textView);
        store_info = (TextView)findViewById(R.id.store_info);

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


        Log.d("TAG", "Intent: "+store_x+"  "+store_y+"  "+store_name+"  "+store_type+"  "+store_addr+"  "+store_call+"  "+store_unikey);
        //firebaseAnalytics 초기화
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFunctions = FirebaseFunctions.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        Log.d("TAG", "x: "+store_x+"  y: "+store_y);

        pinpinEE(store_name, store_x ,store_y, "TEST_"+store_name);

        store_info.setText("주소: "+store_addr+"\n전화번호: "+store_call+"\n업태: "+store_type);

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
                            if (snapshot.getKey().equals(intent.getExtras().getString("store_name"))) {
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
                        for (DataSnapshot snapshot : mdataSnapshot.getChildren()) {
                            Log.d("TAGS", "snapshot key : " + snapshot.getKey());
                            if (snapshot.getKey().equals(store_unikey)) {
                                is_heart = true;
                            }
                            Log.d("TAGS", "favorite : " + snapshot.getKey());
                        }

                        if (is_heart) {
                            mdataSnapshot.getRef().child(store_unikey).removeValue();
                            heart.setImageResource(R.drawable.false_heart);
                            Log.d("TAGS", "in here");
                        }
                        else {
                            mdataSnapshot.getRef().child(store_unikey).setValue(0);
                            heart.setImageResource(R.drawable.true_heart);
                            Log.d("TAGS", "in here2");
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

        //Firebase function이랑 연결
        Task<String> resTask = mFunctions.getHttpsCallable("getDBData").call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception{
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                }).addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Exception e = task.getException();
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                                Log.e("complete", "code: " + code, e);
                            }
                        }
                        else
                            Log.e("heart", "res: " + task.getResult());
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

}
