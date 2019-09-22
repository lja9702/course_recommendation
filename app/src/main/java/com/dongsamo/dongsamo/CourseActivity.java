package com.dongsamo.dongsamo;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class CourseActivity extends AppCompatActivity {

    TextView course_text;
    String textview_txt = null, office="";
    TextView course_location_btn;
    static String course = "";
    int count = 0, eat_count=0;
    AlertDialog alertDialog;
    boolean flag = true;
    String[] want_location;

    String user_id="hi";
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = firebaseAuth.getCurrentUser();

        course_location_btn = (TextView)findViewById(R.id.course_location_btn);
        course_text = (TextView)findViewById(R.id.course_text);
        course_text.setMovementMethod(new ScrollingMovementMethod());

        want_location = new String[5];
        count = 0;

        try{
            Intent intent = getIntent();
            String name = intent.getExtras().getString("store_name");

            course_text.setText(course + " " + name);
        }catch (NullPointerException e){

        }
        //course_text.setText(course);

    }
    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if( firebaseUser == null ){
            Intent intent = new Intent(CourseActivity.this, LoginActivity.class);
            startActivity(intent);
            CourseActivity.this.finish();
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
                    user_id = member.getId();

                }
                catch (NullPointerException e){
                    Toast.makeText(CourseActivity.this, "에러로 인해 로그아웃되었습니다.", Toast.LENGTH_LONG);
                    firebaseAuth.signOut();

                    Intent intent = new Intent(CourseActivity.this, LoginActivity.class);
                    startActivity(intent);
                    CourseActivity.this.finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void onClick_direct_add_btn(View view){
        if(count < 2){
            Toast.makeText(getApplicationContext(), "2개 이상 선택해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        course = String.valueOf(course_text.getText());
        Intent intent = new Intent(CourseActivity.this, UserCourseActivity.class);
        intent.putExtra("location", course_location_btn.getText().toString());
        String[] store_list = course.split("  ");
        intent.putExtra("store_list", store_list);
        intent.putExtra("ori_count", count);
        intent.putExtra("count", count);
        intent.putExtra("store", store_list[1]);

        startActivity(intent);
    }

    public void onClick_trash_btn(View view){
        course_text.setText("");
        eat_count = 0;
        count = 0;
    }

    public void onClick_shop_btn(View view){
        if(count >= 4){
            Toast.makeText(getApplicationContext(), "최대 4개까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
            return;
        }

        if (flag){
            Toast.makeText(CourseActivity.this, "밥플 지역을 먼저 선택해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        count++;
        switch (view.getId()){
            case R.id.restaurant_btn:
                eat_count++;
                textview_txt = String.valueOf(course_text.getText());
                course_text.setText(textview_txt+"  맛집");
                break;
            case R.id.show_btn:
                count--;
                textview_txt = String.valueOf(course_text.getText());
                Intent intent = new Intent(CourseActivity.this, CultureActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.cafe_btn:
                textview_txt = String.valueOf(course_text.getText());
                course_text.setText(textview_txt+"  카페");
                break;
            case R.id.etc_btn:
                count--;
                textview_txt = String.valueOf(course_text.getText());
                Intent intent2 = new Intent(CourseActivity.this, DirectAddActivity.class);

                intent2.putExtra("office", office);

                startActivityForResult(intent2, 1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1){
            if(resultCode==RESULT_OK){
                count++;
                //데이터 받기
                String result = data.getStringExtra("result");
                String location = data.getStringExtra("want_location");
                want_location[want_location.length-1] = location;
                course_text.setText(textview_txt+"  "+result);
            }
        }
    }

    public void onClick_ai_ask_btn(View view){
        if(count < 2){
            Toast.makeText(getApplicationContext(), "2개 이상 선택해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(CourseActivity.this, AIRunningActivity.class);
        intent.putExtra("new_course", course_text.getText().toString());
        intent.putExtra("eat_count", eat_count);
        intent.putExtra("user_id", user_id);
        startActivity(intent);
    }

    public void onClick_course_location_btn(View view){//밥플 지역
        final CharSequence[] items = {
                "강남구", "강동구", "강북구", "강서구", "관악구", "광진구", "구로구", "금천구",
                "노원구", "도봉구", "동대문구", "동작구",
                "마포구",
                "서대문구", "양천구", "용산구","은평구",
                "종로구", "중구",  "성동구",
                 "중랑구", "성북구",
                     "영등포구",  "서초구",
                 "송파구"  };
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CourseActivity.this);

        // 제목셋팅
        alertDialogBuilder.setTitle("밥플지역 선택");
        alertDialogBuilder.setItems(items,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int id) {

                        // 프로그램을 종료한다
                        course_location_btn.setText(items[id]);
                        office = String.valueOf(items[id]);
                        flag = false;
                        dialog.dismiss();
                    }
                });

        // 다이얼로그 생성
        alertDialog = alertDialogBuilder.create();

        // 다이얼로그 보여주기
        alertDialog.show();

    }
}
