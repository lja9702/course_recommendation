package com.dongsamo.dongsamo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    EditText login_editText, passwd_editText;
    TextView find_id, find_pw;
    ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_editText = (EditText)findViewById(R.id.login_editText);
        passwd_editText = findViewById(R.id.login_password_editText);

        find_id = (TextView)findViewById(R.id.find_id);
        find_pw = (TextView)findViewById(R.id.find_pw);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(LoginActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth = FirebaseAuth.getInstance();
        if( firebaseAuth.getCurrentUser() != null ){
            Log.d(TAG, "user email: " + firebaseAuth.getCurrentUser().getEmail());
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            LoginActivity.this.finish();
        }
        else{
            login_editText.setText("");
            passwd_editText.setText("");
        }
    }

    public void onClick_signin(View view){
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    public void onClick_find_id(View view){
        Intent intent = new Intent(this, FindIDActivity.class);
        startActivity(intent);
    }

    public void onClick_find_pw(View view){
        Intent intent = new Intent(this, FindPWActivity.class);
        startActivity(intent);
    }

    public void onClick_login(View view){

        boolean valid = ValidateForm.validateEditText(login_editText, passwd_editText);
        if(!valid){
            return;
        }
        String id = login_editText.getText().toString();
        final String passwd = passwd_editText.getText().toString();
        if( !ValidateForm.validateId(id) ){
            login_editText.setText("");
            passwd_editText.setText("");
            Toast.makeText(this, "아이디는 영어와 숫자를 사용한 4자리 이상 20자리 이하 입니다.", Toast.LENGTH_LONG);
            return;
        }
        databaseReference.child("Users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String email = dataSnapshot.getValue(String.class);
                if( email == null ){
                    Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_LONG).show();
                    return;
                }
                loading();

                firebaseAuth.signInWithEmailAndPassword(email, passwd)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()){
                                    loadingEnd();
                                    InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    im.hideSoftInputFromWindow(login_editText.getWindowToken(), 0);
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    LoginActivity.this.finish();
                                }
                                else{
                                    InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    im.hideSoftInputFromWindow(login_editText.getWindowToken(), 0);
                                    Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_LONG).show();

                                }
                                //loadingEnd();
                            }

                        });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "로그인 실패");
            }
        });

    }

    public void onClick_login_back(View view){
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(login_editText.getWindowToken(), 0);
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
