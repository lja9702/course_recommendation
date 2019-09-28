package com.dongsamo.dongsamo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.app.Activity.RESULT_OK;

public class UserLikeRecyclerViewAdapter extends RecyclerView.Adapter<UserLikeRecyclerViewAdapter.MyViewHoler> {

    private Context mContext;
    private List<UserLikeListitem> mData;

    String siteUrl = "http://openapi.seoul.go.kr:8088/";
    String ID = "6c5474475266627737325756715870";
    String contents = "/json/CrtfcUpsoInfo/1/1000/ / / / /";
    JSONArray building_list;
    String store_name = "";
    String store_type= "", store_addr= "", store_call= "",store_unikey= "", user_id= "";
    float store_x, store_y;


    public UserLikeRecyclerViewAdapter(Context mContext, List<UserLikeListitem> mData){
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHoler onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.user_like_card_view, viewGroup, false);

        return new MyViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHoler myViewHoler, final int i) {
        myViewHoler.name.setText(String.valueOf(mData.get(i).getName()));

        myViewHoler.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                store_name = String.valueOf(mData.get(i).getName());
                try {
                    Intent intent = new Intent(mContext.getApplicationContext(), StoreActivity.class);
                    intent.putExtra("store_name", store_name);
                    mContext.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHoler extends RecyclerView.ViewHolder{

        TextView name;
        CardView cardView ;

        public MyViewHoler(@NonNull View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.name_data);

            cardView = (CardView)itemView.findViewById(R.id.user_like_card_view);
        }
    }

}