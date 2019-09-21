package com.dongsamo.dongsamo;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
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

        startActivity(new Intent(this, LoginActivity.class));

        finish();

    }


}
