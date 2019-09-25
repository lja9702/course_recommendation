package com.dongsamo.dongsamo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class UserLikeRecyclerViewAdapter extends RecyclerView.Adapter<UserLikeRecyclerViewAdapter.MyViewHoler> {

    private Context mContext;
    private List<UserLikeListitem> mData;

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