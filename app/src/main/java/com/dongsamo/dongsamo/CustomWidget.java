package com.dongsamo.dongsamo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dongsamo.dongsamo.firebase_control.Store;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class CustomWidget extends Activity {
    TextView shop_name;
    TextView shop_score;
    ImageButton heart_btn;

    private Intent intent;
    float store_x, store_y;
    String store_name, store_type, store_addr, store_call,store_unikey, user_id, store_food_menu;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private FirebaseAnalytics mFirebaseAnalytics;
    private long mLastClickTime = 0;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.customwidget);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        intent = getIntent();
        store_x = intent.getExtras().getFloat("store_x", 0);
        store_y = intent.getExtras().getFloat("store_y", 0);
        store_name = intent.getExtras().getString("store_name"); //가게명
        store_type = intent.getExtras().getString("store_type"); //가게업태
        store_addr = intent.getExtras().getString("store_address"); //가게 도로명 주소
        store_call = intent.getExtras().getString("store_call"); //전화번호
        store_unikey = intent.getExtras().getString("store_unikey"); //가게id
        store_food_menu = intent.getExtras().getString("store_food_menu"); //가게id
        //UI 객체생성
        shop_name = (TextView)findViewById(R.id.shop_name);
        shop_score = (TextView)findViewById(R.id.shop_score);
        heart_btn = (ImageButton)findViewById(R.id.is_heart);
        //데이터 가져오기
        //store = (Store) getIntent().getSerializableExtra("store");

        shop_name.setText(store_name);
        shop_score.setText(String.valueOf(store_type+" / "+store_food_menu));

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
                            heart_btn.setImageResource(R.drawable.true_heart);
                        } else {
                            heart_btn.setImageResource(R.drawable.false_heart);
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

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if( firebaseUser == null ){
            Intent intent = new Intent(CustomWidget.this, LoginActivity.class);
            startActivity(intent);
            CustomWidget.this.finish();
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
                    Toast.makeText(CustomWidget.this, "에러로 인해 로그아웃되었습니다.", Toast.LENGTH_LONG);
                    firebaseAuth.signOut();

                    Intent intent = new Intent(CustomWidget.this, LoginActivity.class);
                    startActivity(intent);
                    CustomWidget.this.finish();
                }
            }

            @Override
            public void onCancelled(@android.support.annotation.NonNull DatabaseError databaseError) {

                Log.w("TAG", "Failed to read value.", databaseError.toException());
            }
        });
    }

    public void onClick_choose_store(View view){
        Log.d("TAGS", "choose_store");
        Intent intent = new Intent(CustomWidget.this, StoreActivity.class);

        intent.putExtra("store_unikey", store_unikey);
        intent.putExtra("store_name", store_name);
        intent.putExtra("store_x", (float)store_x);
        intent.putExtra("store_y", (float)store_y);
        intent.putExtra("store_type", store_type);
        intent.putExtra("store_call", store_call);
        intent.putExtra("store_address", store_addr);

        //intent.putExtra("store", store.convert_to_card());
        startActivity(intent);
        mOnClose();
    }

    public void onClick_heart(View view) throws InterruptedException {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
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
                            heart_btn.setImageResource(R.drawable.false_heart);
                            Log.d("TAGS", "in here");
                        }
                        else {
                            if (if_index == -1) {
                                mdataSnapshot.getRef().child(String.valueOf(last_cnt)).setValue(store_unikey);
                            }
                            else {
                                mdataSnapshot.getRef().child(String.valueOf(if_index)).setValue(store_unikey);
                            }
                            heart_btn.setImageResource(R.drawable.true_heart);
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

    public void mOnClose(){
          /*
          //데이터 전달하기
          Intent intent = new Intent();
          intent.putExtra("result", "check");
          setResult(RESULT_OK, intent);
          Log.d("TAGS", "hear click");
*/
        //액티비티(팝업) 닫기
        finish();
    }

      /*
      @Override
      public boolean onTouchEvent(MotionEvent event) {
          //바깥레이어 클릭시 안닫히게
          if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
              return false;
          }
          return true;

      }

      @Override
      public void onBackPressed() {
          //안드로이드 백버튼 막기
          return;
      }
      */
}