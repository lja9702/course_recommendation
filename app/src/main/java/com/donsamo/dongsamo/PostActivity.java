package com.donsamo.dongsamo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.donsamo.dongsamo.firebase_control.Store;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

    public void onClick_update(View view){
        writeNewStore("1","쿠우쿠우", "www.kookoo.com");
        writeNewStore("2","구글", "www.google.com");
    }

    public void onClick_toStorage(View view) {
        Intent intent = new Intent(PostActivity.this, FirebaseUIActivity.class);
        startActivity(intent);
    }
    private void writeNewStore(String storeId, String name, String url) {
        Store store = new Store(name, url);

        mDatabase.child("stores").child(storeId).setValue(store);
    }

}
