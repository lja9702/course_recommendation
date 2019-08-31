package com.dongsamo.dongsamo;

import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dongsamo.dongsamo.firebase_control.Store;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class PostActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Store store = dataSnapshot.getValue(Store.class);
                Log.w("TAG", store.getName() + store.getUrl());
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabase.addValueEventListener(postListener);
        mDatabase.addListenerForSingleValueEvent(postListener);
    }

    public void onClick_update(View view) throws IOException {
        String sCurrentLine;
        String path = "/home/kongil/Dongsamo/stores.dat";
        AssetManager am = getResources().getAssets() ;
        int cnt = 0;

        InputStream is = am.open("stores.dat");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));


        //BufferedReader br = new BufferedReader(new FileReader("/home/kongil/다운로드/stores1.txt"));
        while ((sCurrentLine = br.readLine()) != null) {
            Log.d("TAG", sCurrentLine);
            StringTokenizer st = new StringTokenizer(sCurrentLine,"::");
            Log.d("TAG", String.valueOf(st.countTokens()));

            String storeId = st.nextToken();
            String name = st.nextToken();
            String category = st.nextToken();
            Store store = new Store(name, category, "", 0, 0, cnt++%6, false);

            writeNewStore(storeId, store);

        }
}

    public void onClick_toStorage(View view) {
        Intent intent = new Intent(PostActivity.this, FirebaseUIActivity.class);
        startActivity(intent);
    }
    private void writeNewStore(String storeId, Store store) {
        mDatabase.child("stores").child(String.valueOf(storeId)).setValue(store);
    }

}
