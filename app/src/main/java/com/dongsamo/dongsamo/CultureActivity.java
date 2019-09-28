package com.dongsamo.dongsamo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CultureActivity extends AppCompatActivity {
    String siteUrl = "http://openapi.seoul.go.kr:8088/";
    String ID = "715a7862706c6a613131344b74576676";
    String contents = "/json/culturalEventInfo/1/1000/";
    private List<Post3> postlist;
    private RecyclerView list;
    private RecyclerViewAdapter3 list_ap;
    JSONArray building_list;
    ProgressDialog pd;
    int cnt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_culture);

        postlist = new ArrayList<>();
        list = (RecyclerView) findViewById(R.id.recylerview);
        list_ap = new RecyclerViewAdapter3(CultureActivity.this, postlist);
        RecyclerDecoration spaceDecoration = new RecyclerDecoration(70);
        list.addItemDecoration(spaceDecoration);
        list.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!list.canScrollVertically(1)) {
                    showProgress("더 많은 문화공연을 가져오는 중");
                    cnt+=5;
                    try {
                        insert_post(cnt);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    hideProgress();
                    Log.i("Tag", "End of list");
                }
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        list.setLayoutManager(gridLayoutManager);//3행을 가진 그리드뷰로 레이아웃을 만듬

        try {
            new Task().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        list.setAdapter(list_ap);
        list_ap.notifyDataSetChanged();
        list.setAdapter(list_ap);
    }

    private void insert_post(int cnt) throws Exception {

        String[] jsonName = {"CODENAME","TITLE","DATE","PLACE","ORG_NAME","USE_TRGT","USE_FEE","PLAYER","PROGRAM","ETC_DESC",
                "ORG_LINK","MAIN_IMG","RGSTDATE","TICKET","STRTDATE","END_DATE","THEMECODE"};

        JSONObject JS = null;

        String ps_title=null, ps_date=null, ps_place = null, ps_money=null, ps_detail=null;
        Log.d("Course222", "log:"+building_list.length());


        for (int i = cnt; i < building_list.length() && i < cnt+5; i++) {

            JS = building_list.getJSONObject(i);
            if (JS != null) {
                ps_title = JS.optString("TITLE");
                ps_date = JS.optString("DATE");
                ps_place = JS.optString("PLACE");
                ps_money = JS.optString("USE_FEE");
                ps_detail = JS.optString("PROGRAM");
            }

            Log.d("Course222", "log:"+ps_place);
            String api_returns = stringToApi(ps_place);
            Log.d("Course222", "log:"+api_returns);
            if(!api_returns.contains("x")){
                continue;
            }
            String now_data = api_returns.substring(api_returns.indexOf("\"x\""));
            String now_data_array[] = now_data.split("\"");
            float store_x =  Float.parseFloat(now_data_array[3]);
            float store_y =  Float.parseFloat(now_data_array[7]);
            Log.d("Course222", "log:"+store_x+" "+store_y);

            if(store_x != 0.0 && store_y != 0.0) {
                postlist.add(new Post3(ps_title, ps_date, ps_place, ps_money, ps_detail));
            }
        }
        list_ap.notifyDataSetChanged();
    }

    public class Task extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            String result = null;
            try {
                url = new URL(""+siteUrl+ID+contents);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    String str = null;
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    result = buffer.toString();

                    Log.d("LOG", result);
                    JSONObject jsonObject1 = new JSONObject(result);
                    String culturalEventInfo = jsonObject1.getString("culturalEventInfo");
                    Log.d("LOG", "culturalEventInfo: " + culturalEventInfo);
                    JSONObject jsonObject2 = new JSONObject(culturalEventInfo);
                    String row = jsonObject2.getString("row");
                    Log.d("Log", "row: "+row);
                    building_list = new JSONArray(row);

                    reader.close();
                } else {
                    Log.i("통신 결과", conn.getResponseCode() + "에러");
                }

                insert_post(cnt);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }
    }

    public String stringToApi(String store_name){
        GeocodeThreadClass test = new GeocodeThreadClass(store_name);
        Thread t = new Thread(test);
        t.start();
        while(test.get_result() == null);
        return test.get_result();
    }


    public void showProgress(String msg) {
        if( pd == null ) {// 객체를 1회만 생성한다.
            pd = new ProgressDialog(CultureActivity.this); // 생성한다.
            pd.setCancelable(false); // 백키로 닫는 기능을 제거한다.
        }
        pd.setMessage(msg); // 원하는 메시지를 세팅한다.
        pd.show(); // 화면에 띠워라
    }
    // 프로그레스 다이얼로그 숨기기
    public void hideProgress() {
        if (pd != null && pd.isShowing()) { // 닫는다 : 객체가 존재하고, 보일때만
            pd.dismiss();
        }
    }
}
