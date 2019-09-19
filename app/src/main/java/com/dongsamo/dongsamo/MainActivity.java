package com.dongsamo.dongsamo;

import android.content.Intent;
import android.content.res.AssetManager;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import android.annotation.TargetApi;
import android.os.Build;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.dongsamo.dongsamo.firebase_control.Store;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements CameraBridgeViewBase.CvCameraViewListener2 {

    //dong todo opencv & OCR, Tesaract? 처리
    private static final String TAG = "opencv";
    private Mat matInput;
    private Mat matResult;

    //kongil
    TessBaseAPI tessBaseAPI;
    CameraSurfaceView surfaceView;
    ImageButton capture_btn;
    ImageView imageView;

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //퍼미션 상태 확인
            if (!hasPermissions(PERMISSIONS)) {

                //퍼미션 허가 안되어있다면 사용자에게 요청
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }

        //kong

        imageView = findViewById(R.id.imageView);
        surfaceView = findViewById(R.id.activity_surfaceView);
        capture_btn = (ImageButton)findViewById(R.id.capture_btn);

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

    /*training data 있는지 확인 if 없으면 -> createFiles*/
    boolean checkLanguageFile(String dir)
    {
        File file = new File(dir);
        if(!file.exists() && file.mkdirs()) {
            createFiles(dir);
        }
        else if(file.exists()){
            String filePath = dir + "/eng.traineddata";
            File langDataFile = new File(filePath);
            if(!langDataFile.exists())
                createFiles(dir);
        }
        return true;
    }

    /*training data 있는지 확인 if 없으면 -> createFiles*/
    private void createFiles(String dir)
    {
        Log.d("TAG", "createFiles");
        AssetManager assetMgr = this.getAssets();

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = assetMgr.open("eng.traineddata");

            String destFile = dir + "/eng.traineddata";
            outputStream = new FileOutputStream(destFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                Log.d("TAG", "buffer : "+buffer);
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
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

                imageView.setImageBitmap(bitmap);

                /*tesseract-> ML Kit*/
                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
                Log.d("TAGS", "surfaceview capture");

                FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                        .getOnDeviceTextRecognizer();
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



    //여기서부턴 퍼미션 관련 메소드
    static final int PERMISSIONS_REQUEST_CODE = 1000;
    String[] PERMISSIONS  = {"android.permission.CAMERA", "android.permission.ACCESS_FINE_LOCATION"};



    private boolean hasPermissions(String[] permissions) {
        int result;

        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions){

            result = ContextCompat.checkSelfPermission(this, perms);

            if (result == PackageManager.PERMISSION_DENIED){
                //허가 안된 퍼미션 발견
                return false;
            }
        }

        //모든 퍼미션이 허가되었음
        return true;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){

            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;

                    if (!cameraPermissionAccepted)
                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                }
                break;
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder( MainActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id){
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }

    boolean db_find_flag = false;

    private void check(final String result) {
        db_find_flag = false;
        Log.d("TAGS", "result : "+ result);

        /*donging-widget*/
        mDatabase = FirebaseDatabase.getInstance().getReference();


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals("stores")) {
                        for (DataSnapshot csnapshot : snapshot.getChildren()) {
                            Store post = csnapshot.getValue(Store.class);
                            if (result.contains(post.getName())) {
                                db_find_flag = true;
                                Intent intent = new Intent(getApplicationContext(), CustomWidget.class);

                                intent.putExtra("store", post);
                                Log.d("TAGS", "add");
                                if(intent.hasExtra("store")) {
                                    Log.d("TAGS", "find!");
                                    startActivityForResult(intent, 1);
                                }
                                else {
                                    Log.d("TAGS", "no find!");
                                }

                            }
                        }
                    }
                    // ...
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        mDatabase.addValueEventListener(postListener);

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

}
