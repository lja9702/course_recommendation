package com.dongsamo.dongsamo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.donsamo.dongsamo.firebase_control.Store;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class DirectAddActivity extends AppCompatActivity {

    static Activity A_activity;
    static boolean flag=false;
    Spinner sort_spinner, store_spinner;

    Button search_btn;
    EditText store_search;
    LinearLayout linearLayout;

    private RecyclerView store_list;
    private StoreRecyclerViewAdapter myAdapter;
    private List<StoreCard> store;

    private Handler set_post_handler;
    private String path = "new";

    private DatabaseReference mDatabase;// Add by kongil
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference imageRef;
    static String course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_add);

        Intent intent = getIntent();
        String str = intent.getExtras().getString("textview");
        course = str;
        flag=true;

        A_activity = this;
        search_btn = (Button)findViewById(R.id.search_btn);
        sort_spinner = (Spinner) findViewById(R.id.sort_spn);
        store_spinner = (Spinner) findViewById(R.id.store_spn);

        store_search = (EditText)findViewById(R.id.store_search);
        linearLayout = (LinearLayout) findViewById(R.id.direct_linear);

        setSpinner();

        store = new ArrayList<>();
        store_list = (RecyclerView) findViewById(R.id.store_list);
        myAdapter = new StoreRecyclerViewAdapter(this, store);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        store_list.setLayoutManager(gridLayoutManager);//1행을 가진 그리드뷰로 레이아웃을 만듬

        //Add by kongil
        mDatabase = FirebaseDatabase.getInstance().getReference();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals("stores")) {
                        for (DataSnapshot csnapshot : snapshot.getChildren()) {
                            Store post = csnapshot.getValue(Store.class);
                            store.add(new StoreCard(post.getUrl(), post.getName(), post.getStar(),  post.getDistance(), post.isIs_heart()));
                            set_post_handler.sendEmptyMessage(0);

                        }
                    }
                    // ...
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        mDatabase.addValueEventListener(postListener);
        //

        set_post_handler = new Handler() {
            public void handleMessage(Message msg) {
                store_list.setAdapter(myAdapter);
                myAdapter.notifyDataSetChanged();
            }
        };

        store.clear();

        set_post_handler.sendEmptyMessage(0);
    }
    private void setSpinner() {
        sort_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //kong todo spinner item으로 sort
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) adapterView.getChildAt(0)).setGravity(Gravity.CENTER);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        store_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //kong todo spinner item으로 필터링
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) adapterView.getChildAt(0)).setGravity(Gravity.CENTER);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void onClick_search_click(View view){
        String str = store_search.getText().toString();
        Toast.makeText(DirectAddActivity.this, "hihi"+str, Toast.LENGTH_LONG).show();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(store_search.getWindowToken(), 0);

        //kong todo str 이름을 가진 장소만 나오게 하기. str : 검색창(edittext)에 입력한 String

    }

}
