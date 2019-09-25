package com.dongsamo.dongsamo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

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
    String intent_name = null, intent_location = null;
    private List<Post3> postlist;
    private RecyclerView list;
    private RecyclerViewAdapter3 list_ap;
    JSONArray building_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_culture);

        postlist = new ArrayList<>();
        list = (RecyclerView) findViewById(R.id.recylerview);
        list_ap = new RecyclerViewAdapter3(CultureActivity.this, postlist);
        RecyclerDecoration spaceDecoration = new RecyclerDecoration(70);
        list.addItemDecoration(spaceDecoration);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        list.setLayoutManager(gridLayoutManager);//3행을 가진 그리드뷰로 레이아웃을 만듬

        try {
            new Task().execute().get();
            Thread.sleep(1);
            insert_post( );

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

    private void insert_post() throws Exception {
        String[] jsonName = {"CODENAME","TITLE","DATE","PLACE","ORG_NAME","USE_TRGT","USE_FEE","PLAYER","PROGRAM","ETC_DESC",
                "ORG_LINK","MAIN_IMG","RGSTDATE","TICKET","STRTDATE","END_DATE","THEMECODE"};

        JSONObject JS = null;

        String ps_title=null, ps_date=null, ps_place = null, ps_money=null, ps_detail=null;

        for (int i = 0; i < building_list.length(); i++) {
            JS = building_list.getJSONObject(i);
            if (JS != null) {
                for (int j = 0; j < jsonName.length; j++) {
                    if (jsonName[j].equals("TITLE")) {
                        ps_title = JS.optString("TITLE");
                    } else if (jsonName[j].equals("DATE")) {
                        ps_date = JS.optString("DATE");
                    } else if (jsonName[j].equals("PLACE")) {
                        ps_place = JS.optString("PLACE");
                    }
                    else if(jsonName[j].equals("USE_FEE")){
                        ps_money = JS.optString("USE_FEE");
                    }
                    else if(jsonName[j].equals("PROGRAM")){
                        ps_detail = JS.optString("PROGRAM");
                    }
                }
            }
            postlist.add(new Post3(ps_title, ps_date, ps_place, ps_money, ps_detail));
            list_ap.notifyDataSetChanged();
        }
    }

    public class Task extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            String result = null;
            try {
                if(intent_location != null)
                    url = new URL(""+siteUrl+ID+contents+intent_location);
                else
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
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }
    }
}
