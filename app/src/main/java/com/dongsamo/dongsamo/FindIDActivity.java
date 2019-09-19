package com.dongsamo.dongsamo;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FindIDActivity extends AppCompatActivity {
    EditText name_editText;
    ImageButton fin_btn;
    TextView not_found_textView;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id);

        name_editText = (EditText)findViewById(R.id.find_id_email);
        fin_btn = (ImageButton)findViewById(R.id.find_id_ok_btn);
        not_found_textView = (TextView)findViewById(R.id.not_found_textView);
    }

    public void onClick_find_id_ok_btn(View view){
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(name_editText.getWindowToken(), 0);
        final String email = name_editText.getText().toString();
        databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot item: dataSnapshot.getChildren()){
                    item.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Member member = dataSnapshot.getValue(Member.class);

                            if( member.getName().equals(email)) {
                                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(FindIDActivity.this);
                                alert_confirm.setTitle("아이디 정보");
                                alert_confirm.setMessage(member.getId()).setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                });
                                alert_confirm.show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                not_found_textView.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void onClick_login_back(View view){
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(name_editText.getWindowToken(), 0);
    }

}
