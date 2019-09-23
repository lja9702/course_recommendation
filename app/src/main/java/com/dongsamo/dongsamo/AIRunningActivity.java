package com.dongsamo.dongsamo;

import android.content.Intent;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AIRunningActivity extends AppCompatActivity {

    String[] st_list, result_list;
    int eat_count;
    String course;
    private FirebaseFunctions mFunctions;
    List<String> recommendResult;        //추천 리스트
    boolean recommendIsComplete;

    String user_id="hi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airunning);
        mFunctions = FirebaseFunctions.getInstance();
        recommendIsComplete = false;

        Intent i = getIntent();
        course = i.getExtras().getString("new_course");
        eat_count = i.getExtras().getInt("eat_count");
        user_id = i.getExtras().getString("user_id");
        st_list = course.split("  ");
        result_list = new String[5];
        //st_list[1]이 첫 번째. 만약 맛집이 아니면 그냥 두기!

        Log.d("count", course+"  "+eat_count+"  "+user_id);

        //function으로 넘기는 user_id (추천을 진행할 id)
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", user_id);

        AIOnCompleteListener onCompleteListener = new AIOnCompleteListener(this);

        //recommendation 함수 호출
        Task<HashMap<String, Object>> recomTask = mFunctions.getHttpsCallable("Recommendation").call(data)
                .continueWith(new Continuation<HttpsCallableResult, HashMap<String, Object> >() {
                    @Override
                    public HashMap<String, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception{
                        HashMap<String, Object> result = (HashMap<String, Object>) task.getResult().getData();
                        return result;
                    }
                }).addOnCompleteListener(onCompleteListener);


        Log.i("recommed", "msg" + recommendResult);

        Handler handler = new Handler(){
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                Intent intent = new Intent(AIRunningActivity.this, DecidingActivity.class);
                intent.putExtra("new_course", course);
                startActivity(intent);
                finish();
            }

        };
        //handler.sendEmptyMessageDelayed(0,3000);

        //Firebase function이랑 연결


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
            else {
                parent.recommendResult = (List <String> )task.getResult().get("recomStoreList");
                parent.recommendIsComplete = true;
                Log.i("recommend", "res: " + parent.recommendResult);
            }
        }
    }

}
