package com.dongsamo.dongsamo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dongsamo.dongsamo.firebase_control.Store;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//import static com.google.firebase.internal.FirebaseAppHelper.getUid;

public class StoreListActivity extends AppCompatActivity {

    static Activity B_Activity;
    Spinner sort_spinner, store_spinner, location_spinner;
    private RecyclerView store_list;
    private StoreRecyclerViewAdapter myAdapter;
    private List<StoreCard> store;
    private Handler set_post_handler;

    private DatabaseReference mDatabase;// Add by kongil
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference imageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);

        sort_spinner = (Spinner) findViewById(R.id.sort_spn);
        store_spinner = (Spinner) findViewById(R.id.store_spn);
        location_spinner = (Spinner)findViewById(R.id.location_spn);
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
                            store.add(new StoreCard(post.getName(), post.getCategory(), post.getUrl(), post.getX(),  post.getY(), post.getStar(), post.isIs_heart()));
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
                //kong todo spinner 선택된거 기준으로 sort하기
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) adapterView.getChildAt(0)).setGravity(Gravity.CENTER);
                if (i == 0) {
                    sortByDistance();
                    Log.d("TAG","distance");
                }
                else if (i == 1) {
                    sortByStar();
                    Log.d("TAG","star");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        store_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //kong todo spinner 선택된거만 나오게하기. ex) 밥집, 술집, 놀거리
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) adapterView.getChildAt(0)).setGravity(Gravity.CENTER);

                Toast.makeText(getApplicationContext(), ""+store_spinner.getItemAtPosition(i), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        location_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //kong todo spinner 선택된 장소만 나오게 하기
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) adapterView.getChildAt(0)).setGravity(Gravity.CENTER);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void sortByDistance() {
        Collections.sort(store, new Comparator<StoreCard>() {
            @Override
            public int compare(StoreCard o1, StoreCard o2) {
                if (o1.getX() > o2.getX()) {
                    return 1;
                } else if (o1.getX() < o2.getX()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }

    private void sortByStar() {
        Collections.sort(store, new Comparator<StoreCard>() {
            @Override
            public int compare(StoreCard o1, StoreCard o2) {
                if (o1.getStar() < o2.getStar()) {
                    return 1;
                } else if (o1.getStar() > o2.getStar()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }
}
