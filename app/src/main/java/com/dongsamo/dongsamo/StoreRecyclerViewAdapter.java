package com.dongsamo.dongsamo;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dongsamo.dongsamo.firebase_control.Store;

import java.util.List;

import static com.dongsamo.dongsamo.DirectAddActivity.A_activity;
import static com.dongsamo.dongsamo.DirectAddActivity.flag;
import static com.dongsamo.dongsamo.StoreListActivity.B_Activity;
import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class StoreRecyclerViewAdapter extends RecyclerView.Adapter<StoreRecyclerViewAdapter.MyViewHoler>{
    private Context mContext;
    private List<StoreCard> mData;

    public StoreRecyclerViewAdapter(Context mContext, List<StoreCard> mData){
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHoler onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.card_view_item, viewGroup, false);

        return new MyViewHoler(view);
    }

    int j;
    @Override
    public void onBindViewHolder(@NonNull final MyViewHoler myViewHoler, final int i) {
        myViewHoler.card_name.setText(String.valueOf(mData.get(i).getName()));
        myViewHoler.card_distance.setText(""+String.valueOf(getDistance(mData.get(i).getX(), mData.get(i).getY())/1)+" 이내"); // 수정 필요
        myViewHoler.card_star.setText(String.valueOf(mData.get(i).getStar()));

        //kong todo true_heart일 때 DB 좋아요 데이터 true, false_heart일 때 DB 좋아요 false
        if(mData.get(i).isIs_heart())
            myViewHoler.card_heart.setImageResource(R.drawable.true_heart);
        else
            myViewHoler.card_heart.setImageResource(R.drawable.false_heart);

        Glide.with(mContext).load(""+mData.get(i).getUrl()).into(myViewHoler.card_img);

        myViewHoler.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //course.concat(mData.get(i).getName());
                //Toast.makeText(mContext.getApplicationContext(), ""+course+mData.get(i).getName()+"으로 결정!", Toast.LENGTH_LONG).show();

                if(flag) {
                    Intent intent = new Intent(mContext.getApplicationContext(), CourseActivity.class);
                    intent.putExtra("store_name", mData.get(i).getName());
                    mContext.startActivity(intent);
                    flag=false;
                    A_activity.finish();
                }
                else{
                    Intent intent = new Intent(mContext.getApplicationContext(), StoreActivity.class);
                    StoreCard sc = mData.get(i);

                    intent.putExtra("store", sc);

                    mContext.startActivity(intent);
                }
            }
        });

        myViewHoler.card_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mData.get(i).isIs_heart()) {
                    mData.get(i).setIs_heart(false);
                    myViewHoler.card_heart.setImageResource(R.drawable.false_heart);
                }
                else {
                    mData.get(i).setIs_heart(true);
                    myViewHoler.card_heart.setImageResource(R.drawable.true_heart);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHoler extends RecyclerView.ViewHolder{

        TextView card_name;
        TextView card_distance;
        TextView card_star;
        ImageView card_star_img;
        ImageView card_heart;
        ImageView card_img;
        CardView cardView ;

        public MyViewHoler(@NonNull View itemView) {
            super(itemView);
            card_name = (TextView)itemView.findViewById(R.id.card_name);
            card_distance = (TextView)itemView.findViewById(R.id.card_distance);
            card_star = (TextView)itemView.findViewById(R.id.card_star);

            card_heart = (ImageView)itemView.findViewById(R.id.card_heart);
            card_img = (ImageView)itemView.findViewById(R.id.card_img);
            card_star_img = (ImageView)itemView.findViewById(R.id.card_star_img);

            cardView = (CardView)itemView.findViewById(R.id.card_view);
        }
    }

    private double getDistance(double store_x, double store_y) {
        GPSclass gpsclass = new GPSclass(this.mContext);
        Location cur_location = gpsclass.getLocation();
        double cur_x = cur_location.getAltitude(), cur_y = cur_location.getLatitude();
        Log.d("TAG", cur_x + " "+ cur_y);
        double distance = sqrt(pow(store_x-cur_x, 2) + pow(store_y-cur_y, 2));
        return distance;
    }

}
