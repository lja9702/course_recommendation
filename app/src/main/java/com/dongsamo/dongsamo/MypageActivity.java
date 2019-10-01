package com.dongsamo.dongsamo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MypageActivity extends AppCompatActivity {

    TextView mypage_name, mypage_id, mypage_email, please_click_like_textView;
    ImageButton mypage_logout_btn, mypage_withdraw_btn;

    SharedPreferences sub;
    SharedPreferences.Editor editor;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private static final String TAG = "MypageActivity";

    private List<UserLikeListitem> postlist, recomlist;
    private RecyclerView list, recom_rc;
    private UserLikeRecyclerViewAdapter list_ap, recom_ap;
    private Handler set_post_handler;
    String[] store_list;
    JSONArray building_list;
    String pass_ps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        please_click_like_textView = (TextView) findViewById(R.id.please_click_like_textView);

        postlist = new ArrayList<>();
        list = (RecyclerView) findViewById(R.id.mypage_like_list);
        list_ap = new UserLikeRecyclerViewAdapter(MypageActivity.this, postlist);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        list.setLayoutManager(gridLayoutManager);//3행을 가진 그리드뷰로 레이아웃을 만듬

        list.setAdapter(list_ap);

        recomlist = new ArrayList<>();
        recom_rc = (RecyclerView) findViewById(R.id.mypage_recom_list);
        recom_ap = new UserLikeRecyclerViewAdapter(MypageActivity.this, recomlist);
        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(this, 2);
        recom_rc.setLayoutManager(gridLayoutManager2);//3행을 가진 그리드뷰로 레이아웃을 만듬

        list.setAdapter(list_ap);
        recom_rc.setAdapter(recom_ap);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("Parsing").child("Data").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                try {
                    Log.d("Test", String.valueOf(dataSnapshot.getValue()));
                    building_list = new JSONArray(String.valueOf(dataSnapshot.getValue()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

                Log.w("TAGs", "Failed to read value.", databaseError.toException());
            }
        });



        mypage_name = (TextView) findViewById(R.id.mypage_name);
        mypage_id = (TextView) findViewById(R.id.mypage_id);
        mypage_email = (TextView) findViewById(R.id.mypage_email);

        mypage_logout_btn = (ImageButton)findViewById(R.id.mypage_logout_btn);
        mypage_withdraw_btn = (ImageButton)findViewById(R.id.mypage_withdraw_btn);

        mypage_logout_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    mypage_logout_btn.setImageResource(R.drawable.logout_btn_black);
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    mypage_logout_btn.setImageResource(R.drawable.logout_btn);
                }
                return false;
            }
        });

        mypage_withdraw_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    mypage_withdraw_btn.setImageResource(R.drawable.withdraw_btn_black);
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    mypage_withdraw_btn.setImageResource(R.drawable.withdraw_btn);
                }
                return false;
            }
        });

        sub = getSharedPreferences("subscribe", MODE_PRIVATE);

        set_post_handler = new Handler() {
            public void handleMessage(Message msg) {
                list.setAdapter(list_ap);
                list_ap.notifyDataSetChanged();
            }
        };

        set_post_handler.sendEmptyMessage(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        postlist.clear();
        recomlist.clear();
        list_ap.notifyDataSetChanged();
        recom_ap.notifyDataSetChanged();

        if( firebaseUser == null ){
            Intent intent = new Intent(MypageActivity.this, LoginActivity.class);
            startActivity(intent);
            MypageActivity.this.finish();
        }
        updateUI();
    }

    public void updateUI(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
// update name, ... TextView
        databaseReference.child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Member member = dataSnapshot.getValue(Member.class);
                try {
                    mypage_name.setText(member.getName());
                    mypage_id.setText(member.getId());
                    mypage_email.setText(member.getEmail());

                    Log.d("TAGS", member.getId());

                    databaseReference.child("favorite").child(member.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot mdataSnapshot) {

                            for (DataSnapshot snapshot : mdataSnapshot.getChildren()) {
                                Log.d("TAGS", "snap key : " + snapshot.getKey());
                                String name="";
                                try {
                                    name = insert_post((String)snapshot.getValue());
                                    Log.i("Log", "hi Log: "+name);
                                    postlist.add(new UserLikeListitem(name));// Object 형으로 return, 가게번호 to jeong
                                    set_post_handler.sendEmptyMessage(0);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.w(TAG, "Failed to read value.", databaseError.toException());
                        }
                    });

                }
                catch (NullPointerException e){
                    Toast.makeText(MypageActivity.this, "에러로 인해 로그아웃되었습니다.", Toast.LENGTH_LONG);
                    firebaseAuth.signOut();

                    Intent intent = new Intent(MypageActivity.this, LoginActivity.class);
                    startActivity(intent);
                    MypageActivity.this.finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

        databaseReference.child("Users").child(firebaseUser.getUid()).child("Recom_Store").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                recomlist.clear();
                try {
                    pass_ps = dataSnapshot.getValue().toString();
                }
                catch (NullPointerException e){
                    pass_ps = "";
                }

                Log.d("pass_ps", pass_ps);
                if(pass_ps != null && !(pass_ps.equals(""))) {
                    store_list = pass_ps.split("  ");
                    please_click_like_textView.setVisibility(View.GONE);
                    for(int i=1; i<store_list.length; i++){
                        Log.d("pass", store_list[i]);
                        recomlist.add(new UserLikeListitem(store_list[i]));
                        recom_ap.notifyDataSetChanged();
                    }
                }
                else {
                    recomlist.clear();
                    please_click_like_textView.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

                Log.w("TAGs", "Failed to read value.", databaseError.toException());
            }
        });
    }

    public void onClick_mypage_logout_btn(View view){
        firebaseAuth.signOut();
        Intent intent = new Intent(MypageActivity.this, LoginActivity.class);
        startActivity(intent);
        MypageActivity.this.finish();
        Toast.makeText(getApplicationContext(), "로그아웃 완료", Toast.LENGTH_LONG).show();
    }

    public void onClick_mypage_withdraw_btn(View view){
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(MypageActivity.this);
        alert_confirm.setMessage("정말 계정을 삭제 할까요?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if(user == null){
                            Toast.makeText(MypageActivity.this, "회원탈퇴를 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        final String uid = user.getUid();
                        user.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            databaseReference.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    Member member = dataSnapshot.getValue(Member.class);
                                                    databaseReference.child("Email").child(member.getId()).removeValue();
                                                    databaseReference.child("Users").child(uid).removeValue();
                                                    databaseReference.child("favorite").child(member.getId()).removeValue();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                            firebaseAuth.signOut();
                                            finish();
                                        }
                                        else{
                                            Toast.makeText(MypageActivity.this, "회원탈퇴를 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                }
        );

        alert_confirm.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alert_confirm.show();
    }

    JSONObject JS = null;
    private String insert_post(String build_num) throws Exception {
        //UPSO_NM: 가게명, CGG_CODE_NM: 자치구명, BIZCND_CODE_NM : 업태명, Y_DNTS : 지도 Y좌표, X_CNTS: 지도 X좌표, TEL_NO: 전화번호
        //RDN_CODE_NM: 도로명주소,
        String ps_name = null, ps_type = null;
        double store_x = 0, store_y = 0;
        Log.d("LOG", "hi Log   " + building_list.length());

        int cnt = 0;
        for (int i = 0; i < building_list.length(); i++) {

            JS = building_list.getJSONObject(i);
            if (JS != null) {
                ps_name = JS.optString("UPSO_NM");
                ps_type = JS.optString("CRTFC_UPSO_MGT_SNO");
                if (ps_name != null) {
                    if (ps_type.equals(build_num)) {
                        Log.i("Log", "hi Log: "+ps_name);
                        return ps_name;
                    }
                }
            }
        }
        return "";
    }
}