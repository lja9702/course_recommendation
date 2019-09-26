package com.dongsamo.dongsamo;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            Thread.sleep(3000); //3초 대기
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        if(CheckAppFirstExecute()){
            startActivity(new Intent(this, HelpActivity.class));
        }
        else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();

    }


    //앱최초실행확인 (true - 최초실행)
    public boolean CheckAppFirstExecute(){
        SharedPreferences pref = getSharedPreferences("IsFirst" , HelpActivity.MODE_PRIVATE);
        boolean isFirst = pref.getBoolean("isFirst", false);
        if(!isFirst){ //최초 실행시 true 저장
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isFirst", true);
            editor.commit();
        }

        return !isFirst;
    }



}
