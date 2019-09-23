package com.dongsamo.dongsamo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dongsamo.dongsamo.firebase_control.Store;

public class CustomWidget extends Activity {
    TextView shop_name;
    TextView shop_score;
    ImageButton heart_btn;

    private Intent intent;
    float store_x, store_y;
    String store_name, store_type, store_addr, store_call,store_unikey, user_id;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.customwidget);

        intent = getIntent();
        store_x = intent.getExtras().getFloat("store_x", 0);
        store_y = intent.getExtras().getFloat("store_y", 0);
        store_name = intent.getExtras().getString("store_name"); //가게명
        store_type = intent.getExtras().getString("store_type"); //가게업태
        store_addr = intent.getExtras().getString("store_address"); //가게 도로명 주소
        store_call = intent.getExtras().getString("store_call"); //전화번호
        store_unikey = intent.getExtras().getString("store_unikey"); //가게id

        //UI 객체생성
        shop_name = (TextView)findViewById(R.id.shop_name);
        shop_score = (TextView)findViewById(R.id.shop_score);
        heart_btn = (ImageButton)findViewById(R.id.is_heart);
        //데이터 가져오기
        //store = (Store) getIntent().getSerializableExtra("store");

        shop_name.setText(store_name);
        shop_score.setText(String.valueOf(store_call));

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

        intent.putExtra("store_unikey", store_unikey);
        intent.putExtra("store_name", store_name);
        intent.putExtra("store_x", (float)store_x);
        intent.putExtra("store_y", (float)store_y);
        intent.putExtra("store_type", store_type);
        intent.putExtra("store_call", store_call);
        intent.putExtra("store_address", store_addr);

        //intent.putExtra("store", store.convert_to_card());
        startActivity(intent);
        mOnClose();
    }
    public void mOnClose(){
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