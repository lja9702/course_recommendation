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

import com.dongsamo.dongsamo.firebase_control.Store;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.skt.Tmap.TMapPoint;

import java.util.ArrayList;
import java.util.List;

public class DirectAddActivity extends AppCompatActivity {

    static Activity A_activity;
    static boolean flag=false;
    Spinner sort_spinner, store_spinner;

    Button search_btn;
    EditText store_search; //검색창 가져오기
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

        search_btn = (Button)findViewById(R.id.search_btn);

        store_search = (EditText)findViewById(R.id.store_search); //검색창 xml이랑 java파일 연결
        linearLayout = (LinearLayout) findViewById(R.id.direct_linear);


        store = new ArrayList<>();
        store_list = (RecyclerView) findViewById(R.id.store_list);
        myAdapter = new StoreRecyclerViewAdapter(this, store);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        store_list.setLayoutManager(gridLayoutManager);//1행을 가진 그리드뷰로 레이아웃을 만듬

        //Add by kongil
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//
//        ValueEventListener postListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // Get Post object and use the values to update the UI
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    if (snapshot.getKey().equals("stores")) {
//                        for (DataSnapshot csnapshot : snapshot.getChildren()) {
//                            Store post = csnapshot.getValue(Store.class);
//                            //store.add(new StoreCard(post.getUrl(), post.getName(), post.getStar(),  post.getDistance(), post.isIs_heart()));
//                            set_post_handler.sendEmptyMessage(0);
//
//                        }
//                    }
//                    // ...
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Getting Post failed, log a message
//                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
//                // ...
//            }
//        };
//
//        mDatabase.addValueEventListener(postListener);
//        //

        set_post_handler = new Handler() {
            public void handleMessage(Message msg) {
                store_list.setAdapter(myAdapter);
                myAdapter.notifyDataSetChanged();
            }
        };

        store.clear();

        set_post_handler.sendEmptyMessage(0);
    }

    public void onClick_search_click(View view){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(store_search.getWindowToken(), 0);

        //kong todo str 이름을 가진 장소만 나오게 하기. str : 검색창(edittext)에 입력한 String
        String str = store_search.getText().toString(); //예를 들어 naver 검색
        String api_returns = stringToApi(str);

        //DirectAddActivity에 띄울 가게 이름들 리스트
        String store_num_find = api_returns.substring(api_returns.indexOf("\"count\""));
        String store_num_array[] = store_num_find.split("\"");
        String store_num_array2[] = store_num_array[2].split(":");
        String store_num_array3[] = store_num_array2[1].split("\\}");
        int store_num = Integer.parseInt(store_num_array3[0]); //StoreList에 띄울 가게 갯수
        String store_name_find = api_returns.substring(api_returns.indexOf("\"name\""));
        String store_name_array[] = store_name_find.split("\"");
        String[] real_store_name_list = new String [store_num];
        for (int i=0;i<store_num;i++){
            real_store_name_list[i] = store_name_array[30*i+3];
        }


        //lat,lon좌표
        String now_data = api_returns.substring(api_returns.indexOf("\"x\""));
        System.out.println(now_data);
        String now_data_array[] = now_data.split("\"");
        float x_val =  Float.parseFloat(now_data_array[3]);
        float y_val =  Float.parseFloat(now_data_array[7]);


        Intent intent = new Intent(this, StoreActivity.class);

        intent.putExtra("lat_val", x_val);
        intent.putExtra("lon_val", y_val);
        intent.putExtra("name_val", str);
        startActivity(intent);
    }



    //api 받아오는 함수
    public String stringToApi(String target_name){
        GeocodeThreadClass test = new GeocodeThreadClass(target_name);
        Thread t = new Thread(test);
        t.start();
        while(test.get_result() == null);
        return test.get_result();
    }

}

