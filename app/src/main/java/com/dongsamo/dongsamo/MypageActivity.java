package com.dongsamo.dongsamo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

public class MypageActivity extends AppCompatActivity {

    TextView mypage_name, mypage_id, mypage_email;
    ImageButton mypage_logout_btn, mypage_withdraw_btn;

    TextView mypage_recent_course1, mypage_recent_course2, mypage_recent_course3, mypage_recent_course4;
    ImageButton mypage_heart1, mypage_heart2, mypage_heart3, mypage_heart4;

    SharedPreferences sub;
    SharedPreferences.Editor editor;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private static final String TAG = "MypageActivity";

    private List<UserLikeListitem> postlist;
    private RecyclerView list;
    private UserLikeRecyclerViewAdapter list_ap;
    private Handler set_post_handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        postlist = new ArrayList<>();
        list = (RecyclerView) findViewById(R.id.mypage_like_list);
        list_ap = new UserLikeRecyclerViewAdapter(MypageActivity.this, postlist);
//        RecyclerDecoration spaceDecoration = new RecyclerDecoration(70);
//        list.addItemDecoration(spaceDecoration);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        list.setLayoutManager(gridLayoutManager);//3행을 가진 그리드뷰로 레이아웃을 만듬

        list.setAdapter(list_ap);

        mypage_name = (TextView) findViewById(R.id.mypage_name);
        mypage_id = (TextView) findViewById(R.id.mypage_id);
        mypage_email = (TextView) findViewById(R.id.mypage_email);

        mypage_logout_btn = (ImageButton)findViewById(R.id.mypage_logout_btn);
        mypage_withdraw_btn = (ImageButton)findViewById(R.id.mypage_withdraw_btn);

        mypage_recent_course1 = (TextView) findViewById(R.id.mypage_recent_course1);
        mypage_recent_course2 = (TextView) findViewById(R.id.mypage_recent_course2);
        mypage_recent_course3 = (TextView) findViewById(R.id.mypage_recent_course3);
        mypage_recent_course4 = (TextView) findViewById(R.id.mypage_recent_course4);

        mypage_heart1 = (ImageButton) findViewById(R.id.mypage_heart1);
        mypage_heart2 = (ImageButton) findViewById(R.id.mypage_heart2);
        mypage_heart3 = (ImageButton) findViewById(R.id.mypage_heart3);
        mypage_heart4 = (ImageButton) findViewById(R.id.mypage_heart4);

        sub = getSharedPreferences("subscribe", MODE_PRIVATE);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

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

                    databaseReference.child("taste").child(member.getId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot mdataSnapshot) {
                            for (DataSnapshot snapshot : mdataSnapshot.child("favorite").getChildren()) {
                                Log.d("TAGS",snapshot.getKey());

                                postlist.add(new UserLikeListitem(snapshot.getKey()));
                                //list_ap.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.w(TAG, "Failed to read value.", databaseError.toException());
                        }
                    });

                    set_post_handler.sendEmptyMessage(0);
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
    }

    public void onClick_mypage_logout_btn(View view){
        firebaseAuth.signOut();
        Intent intent = new Intent(MypageActivity.this, LoginActivity.class);
        startActivity(intent);
        MypageActivity.this.finish();
        Toast.makeText(getApplicationContext(), "로그아웃 완료", Toast.LENGTH_LONG).show();
    }

    public void onClick_mypage_heart(View view){

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
}
