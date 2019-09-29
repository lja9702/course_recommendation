package com.dongsamo.dongsamo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class AIRunningActivity extends AppCompatActivity {

    String[] st_list, result_list, recommed_list;
    int eat_count;
    String course;
    private FirebaseFunctions mFunctions;
    List<String> recommendResult;        //추천 리스트
    List<String> unionStoreResult;      //추천 + 좋아요 리스트
    boolean isRandomData = false;
    boolean recommendIsComplete;

    String user_id="hi", ori_course, office, pass_course="", recom_store="", real_place="";

    String siteUrl = "http://openapi.seoul.go.kr:8088/";
    String ID = "6c5474475266627737325756715870";
    String contents = "/json/CrtfcUpsoInfo/1/1000/ / / / /";
    JSONArray building_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airunning);
        mFunctions = FirebaseFunctions.getInstance();
        recommendIsComplete = false;

        Intent intent1 = getIntent();
        office = intent1.getExtras().getString("office");
        ori_course = intent1.getExtras().getString("new_course");
        course = intent1.getExtras().getString("new_course");
        eat_count = intent1.getExtras().getInt("eat_count");
        user_id = intent1.getExtras().getString("user_id");
        real_place = intent1.getExtras().getString("real_place");

        st_list = course.split("  ");

//        if(eat_count == 0){
//            Intent intent = new Intent(AIRunningActivity.this, DecidingActivity.class);
//
//            course = "  "+course;
//            intent.putExtra("new_course", course);
//            intent.putExtra("office", office);
//            intent.putExtra("real_place", real_place);
//            startActivity(intent);
//            finish();
//        }

        Log.d("count", course+"  "+eat_count+"  "+user_id);

        //function으로 넘기는 user_id (추천을 진행할 id)
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", user_id);
        AIOnCompleteListener onCompleteListener = new AIOnCompleteListener(AIRunningActivity.this);
        //recommendation 함수 호출
        Task<HashMap<String, Object>> recomTask = mFunctions.getHttpsCallable("Recommendation").call(data)
                .continueWith(new Continuation<HttpsCallableResult, HashMap<String, Object> >() {
                    @Override
                    public HashMap<String, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception{
                        HashMap<String, Object> result = (HashMap<String, Object>) task.getResult().getData();
                        return result;
                    }
                }).addOnCompleteListener(onCompleteListener);
    }

    class AIOnCompleteListener implements OnCompleteListener<HashMap<String, Object> >{
        AIRunningActivity parent;

        public AIOnCompleteListener(AIRunningActivity acti){
            parent = acti;
        }

        @Override
        public void onComplete(@NonNull Task<HashMap<String, Object> > task) {
            if (!task.isSuccessful()) {
                Exception e = task.getException();
                if (e instanceof FirebaseFunctionsException) {
                    FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                    FirebaseFunctionsException.Code code = ffe.getCode();
                    Object details = ffe.getDetails();
                }
                Log.e("complete", "code: " + e.getMessage(), e);
            }
            else {  //firebase function으로 부터 추천 리스트와 추천+좋아요 리스트 그리고 좋아요를 누른 적이 한번이라도 있는지 확인하는 데이터 receive
                parent.unionStoreResult = (List <String> )task.getResult().get("unionStoreList");
                parent.isRandomData = (boolean)task.getResult().get("isRandomData");
                if(parent.isRandomData == false){
                    parent.recommendResult = (List <String> )task.getResult().get("recomStoreList");
                }
                parent.recommendIsComplete = true;
                Log.i("recommend", "res: " + parent.recommendResult);
                Log.i("union", "res: " + parent.unionStoreResult);
                try {
                    new FoodTask().execute().get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public class FoodTask extends AsyncTask<String, String, String> {
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
                        String CrtfcUpsoInfo = jsonObject1.getString("CrtfcUpsoInfo");
                        Log.d("LOG", "CrtfcUpsoInfo: " + CrtfcUpsoInfo);
                        JSONObject jsonObject2 = new JSONObject(CrtfcUpsoInfo);
                        String row = jsonObject2.getString("row");
                        Log.d("Log", "row: "+row);
                        building_list = new JSONArray(row);

                        reader.close();

                        insert_post( );
                    } else {
                        Log.i("통신 결과", conn.getResponseCode() + "에러");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return result;
            }
        }

        JSONObject JS = null;
        private void insert_post() throws Exception {
            //UPSO_NM: 가게명, CGG_CODE_NM: 자치구명, BIZCND_CODE_NM : 업태명, Y_DNTS : 지도 Y좌표, X_CNTS: 지도 X좌표, TEL_NO: 전화번호
            //RDN_CODE_NM: 도로명주소,
            result_list = new String[parent.unionStoreResult.size()+10];

            String ps_name = null, ps_type = null;
            Log.d("LOG", "hi Log   "+building_list.length());
            int cnt = 0;
            for(int k=0; k<parent.unionStoreResult.size(); k++) {
                for (int i = 0; i < building_list.length(); i++) {

                    JS = building_list.getJSONObject(i);
                    if (JS != null) {
                        ps_name = JS.optString("UPSO_NM");
                        ps_type = JS.optString("CRTFC_UPSO_MGT_SNO");
                        if (ps_name != null) {
                            if (parent.unionStoreResult.get(k).equals(ps_type)){
                                Log.i("Log", "hi Log");
                                result_list[cnt++] = ps_name;
                            }
                        }
                    }
                }
            }
            if(parent.isRandomData == false) {
                cnt = 0;
                recommed_list = new String[parent.recommendResult.size() + 10];
                for (int k = 0; k < parent.recommendResult.size(); k++) {
                    for (int i = 0; i < building_list.length(); i++) {

                        JS = building_list.getJSONObject(i);
                        if (JS != null) {
                            ps_name = JS.optString("UPSO_NM");
                            ps_type = JS.optString("CRTFC_UPSO_MGT_SNO");
                            if (ps_name != null) {
                                if (parent.recommendResult.get(k).equals(ps_type)) {
                                    Log.i("Log", "hi Log");
                                    recommed_list[cnt++] = ps_name;
                                }
                            }
                        }
                    }
                }
                for(int i=0; i<cnt; i++)
                    recom_store += ("  "+recommed_list[i]);
            }

            else{
                for(int i=0; i<cnt; i++)
                    recom_store += ("  "+result_list[i]);
            }

            pass_course="";

            for(int i=0; i<st_list.length; i++){
                if(st_list[i].equals("맛집")){
                    pass_course += ("  "+result_list[i]);
                }
                else
                    pass_course += ("  " + st_list[i]);

            }

            Log.d("new_course", pass_course);
            Intent intent = new Intent(AIRunningActivity.this, DecidingActivity.class);
            intent.putExtra("recom_store", recom_store);
            intent.putExtra("new_course", pass_course);
            intent.putExtra("office", office);
            intent.putExtra("real_place", real_place);
            intent.putExtra("isRandom", isRandomData);
            startActivity(intent);
            finish();
        }
    }
}