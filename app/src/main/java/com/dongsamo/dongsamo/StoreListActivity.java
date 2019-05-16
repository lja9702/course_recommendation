package com.donsamo.Dongsamo;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class StoreListActivity extends AppCompatActivity {

    Spinner sort_spinner, store_spinner;
    TextView sort_test, store_test;
    private RecyclerView store_list;
    private StoreRecyclerViewAdapter myAdapter;
    private List<StoreCard> store;
    private Handler set_post_handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);

        sort_spinner = (Spinner) findViewById(R.id.sort_spn);
        store_spinner = (Spinner) findViewById(R.id.store_spn);
        sort_test = (TextView) findViewById(R.id.sort_test_txt);
        store_test = (TextView) findViewById(R.id.store_test_txt);
        setSpinner();

        store = new ArrayList<>();
        store_list = (RecyclerView) findViewById(R.id.store_list);
        myAdapter = new StoreRecyclerViewAdapter(this, store);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        store_list.setLayoutManager(gridLayoutManager);//1행을 가진 그리드뷰로 레이아웃을 만듬

        set_post_handler = new Handler() {
            public void handleMessage(Message msg) {
                store_list.setAdapter(myAdapter);
                myAdapter.notifyDataSetChanged();
            }
        };

        store.clear();

        store.add(new StoreCard("https://cdn.pixabay.com/photo/2018/08/29/19/03/steak-3640560_1280.jpg", "정주네맛집", 4.3, 150.3, true));
        store.add(new StoreCard("https://cdn.pixabay.com/photo/2016/08/31/21/47/burger-1634705_1280.jpg", "공일네맛집", 5.0, 100, false));
        store.add(new StoreCard("https://cdn.pixabay.com/photo/2017/01/06/16/46/sushi-1958247_1280.jpg", "진아네맛집", 1, 210, true));
        store.add(new StoreCard("https://cdn.pixabay.com/photo/2016/09/15/19/24/salad-1672505_1280.jpg", "동인네맛집", 2, 132.6, false));
        store.add(new StoreCard("https://cdn.pixabay.com/photo/2014/01/09/10/14/kimchi-fried-rice-241051_1280.jpg", "진네맛집", 3, 1000, true));

        set_post_handler.sendEmptyMessage(0);
    }

    private void setSpinner() {
        sort_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) adapterView.getChildAt(0)).setGravity(Gravity.CENTER);
                sort_test.setText("sort : " + adapterView.getItemAtPosition(i));

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        store_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) adapterView.getChildAt(0)).setGravity(Gravity.CENTER);
                store_test.setText("store : " + adapterView.getItemAtPosition(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
