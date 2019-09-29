package com.dongsamo.dongsamo;

import android.content.Intent;
import android.content.res.AssetManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.support.annotation.NonNull;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.dongsamo.dongsamo.firebase_control.Store;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.googlecode.tesseract.android.TessBaseAPI;

import org.json.JSONArray;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity
        implements CameraBridgeViewBase.CvCameraViewListener2 {

    //dong todo opencv & OCR, Tesaract? 처리
    private static final String TAG = "opencv";
    private Mat matInput;
    private Mat matResult;

    //kongil
    TessBaseAPI tessBaseAPI;
    CameraSurfaceView surfaceView;
    ImageButton capture_btn, main_course_ask_btn, main_shop_list_btn, main_mypage_btn;

    double lon, lat;
    String now_location;
    String dong_location;
    String siteUrl = "http://openapi.seoul.go.kr:8088/";
    String ID = "6c5474475266627737325756715870";
    String contents = "/json/CrtfcUpsoInfo/1/1000/";
    JSONArray building_list;
    String pass_ps;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    DatabaseReference mDatabase;// Add by kongil

    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                   // mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //kong
        try {
            new FoodTask().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        databaseReference.child("Parsing").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().child("Data").removeValue();
                dataSnapshot.getRef().child("Data").setValue(pass_ps);
            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

                Log.w("TAGs", "Failed to read value.", databaseError.toException());
            }
        });

        surfaceView = findViewById(R.id.activity_surfaceView);
        capture_btn = (ImageButton)findViewById(R.id.capture_btn);
        main_course_ask_btn = (ImageButton) findViewById(R.id.main_course_ask_btn);
        main_shop_list_btn = (ImageButton) findViewById(R.id.main_shop_list_btn);
        main_mypage_btn = (ImageButton) findViewById(R.id.main_mypage_btn);

        main_shop_list_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    main_shop_list_btn.setImageResource(R.drawable.shop_list_black);
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    main_shop_list_btn.setImageResource(R.drawable.shop_list);
                }
                return false;
            }
        });

        main_course_ask_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    main_course_ask_btn.setImageResource(R.drawable.course_ask_black);
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    main_course_ask_btn.setImageResource(R.drawable.course_ask);
                }
                return false;
            }
        });

        main_mypage_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    main_mypage_btn.setImageResource(R.drawable.mypage_btn_black);
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    main_mypage_btn.setImageResource(R.drawable.mypage_btn);
                }
                return false;
            }
        });

        try {
            GPSclass gpSclass = new GPSclass(this);
            Location loc = gpSclass.getLocation();
            lon = loc.getLongitude();
            lat = loc.getLatitude();
        }
        catch (NullPointerException e){
            Toast.makeText(getApplicationContext(), "위치정보를 키시고 앱을 재실행해주세요.", Toast.LENGTH_LONG).show();
        }

        String api_returns = stringToApi();
        //내 위치 기반으로 무슨 구 인지 찾아오기
        String store_num_find = api_returns.substring(api_returns.indexOf("\"name\""));
        String store_num_array[] = store_num_find.split("\"");
        try {
            now_location = store_num_array[75];
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), "GPS 오류", Toast.LENGTH_LONG).show();
        }
    }

    public String stringToApi(){
        ReGeocodeThreadClass test = new ReGeocodeThreadClass(lat, lon);
        Thread t = new Thread(test);
        t.start();
        while(test.get_result() == null);

        return test.get_result();
    }

    JSONObject JS = null;
    private boolean insert_post(String result) throws Exception {
        //UPSO_NM: 가게명, CGG_CODE_NM: 자치구명, BIZCND_CODE_NM : 업태명, Y_DNTS : 지도 Y좌표, X_CNTS: 지도 X좌표, TEL_NO: 전화번호
        //RDN_CODE_NM: 도로명주소,

        String ps_name = null, ps_type = null, ps_call= null, ps_address= null, ps_unikey, place_name, food_menu;
        double store_x = 0, store_y = 0;
        Log.d("TAGS", "bef insert post!");
        Log.d("TAGS", "hi Log   "+building_list.length());
        Log.d("TAGS", "af insert post!");

        for (int i = 0; i < building_list.length(); i++) {

            JS = building_list.getJSONObject(i);
            if (JS != null) {
                place_name = JS.optString("CGG_CODE_NM");
                if(place_name.equals(now_location)) {
                    ps_name = JS.optString("UPSO_NM");
                    Log.d("Store", "업소: " + ps_name);
                    if (ps_name != null) {
                        Log.d("Store", result + "  " + ps_name);
                        if (result.contains(ps_name) || ps_name.contains(result)) {
                            Log.d("Success", "HI success");

                            ps_name = JS.optString("UPSO_NM");
                            ps_call = JS.optString("TEL_NO");
                            ps_address = JS.optString("RDN_CODE_NM") + JS.optString("RDN_DETAIL_ADDR");
                            ps_type = JS.optString("BIZCND_CODE_NM");
                            ps_unikey = JS.optString("CRTFC_UPSO_MGT_SNO");
                            store_x = JS.optDouble("X_CNTS");
                            store_y = JS.optDouble("Y_DNTS");
                            food_menu = JS.optString("FOOD_MENU");

                            wintent.putExtra("store_unikey", ps_unikey);
                            wintent.putExtra("store_name", ps_name);
                            wintent.putExtra("store_x", (float) store_x);
                            wintent.putExtra("store_y", (float) store_y);
                            wintent.putExtra("store_type", ps_type);
                            wintent.putExtra("store_call", ps_call);
                            wintent.putExtra("store_address", ps_address);
                            wintent.putExtra("store_food_menu", food_menu);

                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /*주변상점보기*/
    public void onClick_main_shop_list_btn(View view){
         Intent intent = new Intent(MainActivity.this, StoreListActivity.class);
        startActivity(intent);
    }
    /*코스추천*/
    public void onClick_main_course_ask_btn(View view){
        Intent intent = new Intent(MainActivity.this, CourseActivity.class);
        startActivity(intent);
    }

    /*카메라 캡쳐 capture 함수 호출*/
    public void onClick_capture_btn(View view) {
        Log.d("TAGS", "onClick_capture");
        capture();
    }

    /*카메라 캡쳐*/
    private void capture()
    {
        surfaceView.capture(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                bitmap = GetRotatedBitmap(bitmap, 0); //90 -> 0

                capture_btn.setEnabled(false);
                capture_btn.setImageResource(R.drawable.loading_btn);

                /*tesseract-> ML Kit*/
                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
                Log.d("TAGS", "surfaceview capture");

                FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                        .getCloudTextRecognizer();
                Task<FirebaseVisionText> result =
                        detector.processImage(image)
                                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                    @Override
                                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                        // Task completed successfully
                                        String resultText = firebaseVisionText.getText();
                                        Log.d("TAGS", "resultText : "+resultText);

                                        check(resultText);
                                    }
                                })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Task failed with an exception
                                                Log.d("TAGS", "FAIL");
                                                capture_btn.setEnabled(true);
                                                capture_btn.setImageResource(R.drawable.capture);
                                                Toast.makeText(getApplicationContext(), "FAIL", Toast.LENGTH_LONG).show();

                                                // ...
                                            }
                                        }
                                );
                camera.startPreview();
            }
        });
    }

    public synchronized static Bitmap GetRotatedBitmap(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != b2) {
                    bitmap = b2;
                }
            } catch (OutOfMemoryError ex) {
                ex.printStackTrace();
            }
        }
        return bitmap;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        //if (mOpenCvCameraView != null)
         //   mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if( firebaseUser == null ){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }

        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "onResum :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        matInput = inputFrame.rgba();

        if ( matResult == null )

            matResult = new Mat(matInput.rows(), matInput.cols(), matInput.type());

        return matResult;
    }


    boolean db_find_flag = false;
    Intent wintent;
    private void check(final String result) {
        db_find_flag = false;
        Log.d("TAGS", "result : "+ result);

        /*donging-widget*/

        try {
            Log.d("TAGS", "FOodTask");
            Log.d("TAGS", "Before insert");
            Store post = new Store();
            wintent = new Intent(getApplicationContext(), CustomWidget.class);

            if(insert_post(result)){
                db_find_flag = true;

                Log.d("TAGS", "After insert");
                wintent.putExtra("store", post);
                Log.d("TAGS", "name : " + post.getName() + post.getX());
                Log.d("TAGS", "add");
                if(wintent.hasExtra("store")) {
                    Log.d("TAGS", "find!");
                    startActivityForResult(wintent, 1);
                }
                else {
                    Log.d("TAGS", "no find!");
                }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!db_find_flag) {
            Toast.makeText(getApplicationContext(), result+"의 평가를 찾을 수 없습니다.", Toast.LENGTH_LONG).show();
        }
        //mDatabase.addListenerForSingleValueEvent(postListener);

        Log.d("TAGS", "done??");

        capture_btn.setEnabled(true);
        capture_btn.setImageResource(R.drawable.capture);
    }

    public void onClick_main_mypage_btn(View view){
        Intent intent = new Intent(this, MypageActivity.class);
        startActivity(intent);
    }

    public void onClick_main_help_btn(View view){
        Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
        intent.putExtra("flag", false);
        startActivity(intent);
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
                    if(building_list != null)
                        pass_ps = row;

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
