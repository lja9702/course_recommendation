package com.dongsamo.dongsamo;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    EditText signup_email, signup_id, signup_pw, signup_name;
    ImageButton signup_fin;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signup_email = (EditText)findViewById(R.id.signup_email);
        signup_id = (EditText)findViewById(R.id.signup_id);
        signup_pw = (EditText)findViewById(R.id.signup_pw);
        signup_name = (EditText)findViewById(R.id.signup_name);

        signup_fin = (ImageButton)findViewById(R.id.signup_ok_btn);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(SignupActivity.this);

    }

    public void onClick_signin_back(View view){
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(signup_id.getWindowToken(), 0);
    }

    public void onClick_signup_ok_btn(View view){
        boolean valid = ValidateForm.validateEditText(signup_id, signup_email, signup_pw, signup_name);
        if(!valid){
            Log.d("ValidForm", "Some EditText is null");
            loadingEnd();
            return;
        }
        Log.d("ValidForm", "Start Valid");
        if( !ValidateForm.validateEmail(signup_email.getText().toString()) ){
            Log.d("ValidForm", "In Email");
            Toast.makeText(SignupActivity.this, "이메일 형식을 지켜주세요.", Toast.LENGTH_LONG).show();
            loadingEnd();
            return;
        }
        Log.d("ValidForm", "Pass Email");
        if( !ValidateForm.validateId(signup_id.getText().toString()) ){
            Log.d("ValidForm", "In ID");
            Toast.makeText(SignupActivity.this, "아이디는 영어와 숫자를 사용한 4자리 이상 20자리 이하 입니다.", Toast.LENGTH_LONG).show();
            loadingEnd();
            return;
        }
        Log.d("ValidForm", "Pass ID");
        if( !ValidateForm.validatePw(signup_pw.getText().toString())){
            Log.d("ValidForm", "In PW");
            Toast.makeText(getApplicationContext(), "비밀번호는 숫자 6자리 입니다.", Toast.LENGTH_LONG).show();
            loadingEnd();
            return;
        }
        Log.d("ValidForm", "Pass PW");

        final String id = signup_id.getText().toString();
        final String email = signup_email.getText().toString();
        final String name = signup_name.getText().toString();
        Log.d(TAG, "email : " + email + "id : " + id);

        databaseReference.child("Email").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loading();
                String email2 = dataSnapshot.getValue(String.class);
                Log.d(TAG, "email : " + email2);
                if( email2 == null ){
                    firebaseAuth.createUserWithEmailAndPassword(email, signup_pw.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Log.d("check", "after : "+email+name);
                                        Log.d(TAG, "Success sign up");
                                        final Member member = new Member(id, email, name);
                                        firebaseAuth.signInWithEmailAndPassword(email, signup_pw.getText().toString())
                                                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if(task.isSuccessful()){
                                                            FirebaseUser user = null;
                                                            while(user == null) {
                                                                user = firebaseAuth.getCurrentUser();
                                                            }
                                                            databaseReference.child("Users").child(user.getUid()).setValue(member);
                                                            SignupActivity.this.finish();
                                                        }
                                                        else{
                                                            Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });

                                        Log.d(TAG, "user email:" + email);
                                    }
                                    else{
                                        Toast.makeText(SignupActivity.this, "다시 시도해주세요(이메일 중복 / 아이디 중복)", Toast.LENGTH_LONG).show();
                                    }
                                    loadingEnd();
                                }

                            });
                }
                else{
                    loadingEnd();
                    Toast.makeText(SignupActivity.this, "다시 시도해주세요(이메일 중복 / 아이디 중복)", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void loading() {
        //로딩
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("잠시만 기다려 주세요");
                        progressDialog.show();
                    }
                }, 0);
    }

    public void loadingEnd() {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 0);
    }

}
