package com.dongsamo.dongsamo;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FindPWActivity extends AppCompatActivity {
    EditText id_editText, email_editText;
    ImageButton fin_btn;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw);

        id_editText = (EditText)findViewById(R.id.find_pw_id);
        email_editText = (EditText)findViewById(R.id.find_pw_email);
        fin_btn = (ImageButton)findViewById(R.id.find_pw_ok_btn);


    }

    public void onClick_find_pw_ok_btn(View view){

        if(!ValidateForm.validateEditText(id_editText, email_editText)){
            return;
        }

        final String id = id_editText.getText().toString();
        final String email = email_editText.getText().toString();

        databaseReference.child("Email").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String email2 = dataSnapshot.getValue(String.class);
                if( email.equals(email2) ){
                    firebaseAuth.sendPasswordResetEmail(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        AlertDialog.Builder alertdialog = new AlertDialog.Builder(FindPWActivity.this);
        // 다이얼로그 메세지
        alertdialog.setMessage("성공적으로 메일을 전송했습니다.\n메일을 확인하여 재설정을 완료하세요.");

        // 확인버튼
        alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FindPWActivity.this.finish();
            }
        });
        // 메인 다이얼로그 생성
        AlertDialog alert = alertdialog.create();
        // 타이틀
        alert.setTitle("전송완료");
        // 다이얼로그 보기
        alert.show();

    }


    public void onClick_login_back(View view){
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(email_editText.getWindowToken(), 0);
    }

}
