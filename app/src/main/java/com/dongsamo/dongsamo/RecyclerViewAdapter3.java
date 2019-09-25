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

public class RecyclerViewAdapter3 extends RecyclerView.Adapter<RecyclerViewAdapter3.MyViewHoler> {

    private Context mContext;
    private List<Post3> mData;

    public RecyclerViewAdapter3(Context mContext, List<Post3> mData){
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHoler onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.card_view3, viewGroup, false);

        return new MyViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHoler myViewHoler, final int i) {
        myViewHoler.title.setText(String.valueOf(mData.get(i).getTitle()));
        myViewHoler.date.setText(String.valueOf(mData.get(i).getDate()));
        myViewHoler.place.setText(String.valueOf(mData.get(i).getPlace()));
        myViewHoler.money.setText(String.valueOf(mData.get(i).getMoney()));
        myViewHoler.detail.setText(String.valueOf(mData.get(i).getDetail()));

        myViewHoler.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CourseActivity.class);
                intent.putExtra("result", String.valueOf(mData.get(i).getPlace()));
                ((Activity)mContext).setResult(RESULT_OK, intent);
                ((Activity)mContext).finish();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHoler extends RecyclerView.ViewHolder{

        TextView title, date, place, money, detail;
        CardView cardView ;

        public MyViewHoler(@NonNull View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.title_data);
            date = (TextView)itemView.findViewById(R.id.date_data);
            place = (TextView)itemView.findViewById(R.id.place_data);
            money = (TextView)itemView.findViewById(R.id.money_data);
            detail = (TextView)itemView.findViewById(R.id.program_detail);

            cardView = (CardView)itemView.findViewById(R.id.card_view3);
        }
    }

}