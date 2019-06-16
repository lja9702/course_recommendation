package com.dongsamo.dongsamo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dongsamo.dongsamo.firebase_control.Store;

public class CustomWidget extends Activity {
    TextView shop_name;
    TextView shop_score;
    ImageButton heart_btn;

    private Store store;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.customwidget);

        //UI 객체생성
        shop_name = (TextView)findViewById(R.id.shop_name);
        shop_score = (TextView)findViewById(R.id.shop_score);
        heart_btn = (ImageButton)findViewById(R.id.is_heart);
        //데이터 가져오기
        store = (Store) getIntent().getSerializableExtra("store");

        shop_name.setText(store.getName());
        shop_score.setText(String.valueOf(store.getStar()));

        /*
        Intent intent = getIntent();
        String get_shopName = intent.getStringExtra("name");
        String get_shopScore = intent.getStringExtra("score");
        shop_name.setText(get_shopName);
        shop_score.setText(get_shopScore);
        */
          /*
          heart_btn.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  //데이터 전달하기
                  Intent intent = new Intent();
                  intent.putExtra("result", "check");
                  setResult(RESULT_OK, intent);
                  Log.d("TAGS", "hear click");
              }
          });
          */
    }
    public void onClick_choose_store(View view){
        Log.d("TAGS", "choose_store");
        Intent intent = new Intent(CustomWidget.this, StoreActivity.class);
        intent.putExtra("store", store.convert_to_card());
        startActivity(intent);
    }
    public void mOnClose(View v){
          /*
          //데이터 전달하기
          Intent intent = new Intent();
          intent.putExtra("result", "check");
          setResult(RESULT_OK, intent);
          Log.d("TAGS", "hear click");
*/
        //액티비티(팝업) 닫기
        finish();
    }

      /*
      @Override
      public boolean onTouchEvent(MotionEvent event) {
          //바깥레이어 클릭시 안닫히게
          if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
              return false;
          }
          return true;

      }

      @Override
      public void onBackPressed() {
          //안드로이드 백버튼 막기
          return;
      }
      */
}