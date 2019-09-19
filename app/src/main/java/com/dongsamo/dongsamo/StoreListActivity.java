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

    }

}
