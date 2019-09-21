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

public class DirectAddRecyclerViewAdapter extends RecyclerView.Adapter<DirectAddRecyclerViewAdapter.MyViewHoler> {

    private Context mContext;
    private List<DirectAddItem> mData;

    public DirectAddRecyclerViewAdapter(Context mContext, List<DirectAddItem> mData){
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHoler onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.want_card_view, viewGroup, false);

        return new MyViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHoler myViewHoler, final int i) {
        myViewHoler.name.setText(String.valueOf(mData.get(i).getName()));
        myViewHoler.addr.setText(String.valueOf(mData.get(i).getAddr()));
        myViewHoler.call.setText(String.valueOf(mData.get(i).getCall()));

        myViewHoler.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CourseActivity.class);
                intent.putExtra("result", String.valueOf(mData.get(i).getName()));
                intent.putExtra("want_location", mData.get(i).getLocation());
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

        TextView name, addr, call;
        CardView cardView ;

        public MyViewHoler(@NonNull View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.name_data);
            addr = (TextView)itemView.findViewById(R.id.addr_data);
            call = (TextView)itemView.findViewById(R.id.call_data);

            cardView = (CardView)itemView.findViewById(R.id.want_card_view);
        }
    }

}