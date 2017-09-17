package com.seoulprojet.seoulpoem.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.seoulprojet.seoulpoem.R;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MyPage extends AppCompatActivity {

    ImageView mypage_profile_img;
    Button mypage_hamburger_btn;
    Button mypage_setting_btn;
    TextView mypage_name_txt;
    TextView mypage_message_txt;
    Button mypage_upload_btn;
    Button mypage_photo_btn;
    Button mypage_poem_btn;
    ////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        ////////////////////////////////
        mypage_profile_img = (ImageView)findViewById(R.id.mypage_profile_img);
        mypage_hamburger_btn = (Button)findViewById(R.id.mypage_hamburger_btn);
        mypage_setting_btn = (Button)findViewById(R.id.mypage_setting_btn);
        mypage_name_txt = (TextView)findViewById(R.id.mypage_name_txt);
        mypage_message_txt = (TextView)findViewById(R.id.mypage_message_txt);
        mypage_upload_btn = (Button)findViewById(R.id.mypage_upload_btn);
        mypage_photo_btn = (Button)findViewById(R.id.mypage_photo_btn);
        mypage_poem_btn = (Button)findViewById(R.id.mypage_poem_btn);
        ////////////////////////////////


        /////// 페이지 이동 //////
        mypage_setting_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyPageSetting.class);
                startActivity(intent);
             }
        });

        /////////fragment////////

        mypage_photo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getFragmentManager()
                       .beginTransaction()
                       .replace(R.id.mypage_fragment, new MyPagePhotoFragment())
                       .commit();
            }
        });

        mypage_poem_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mypage_fragment, new MyPagePoemFragment())
                        .commit();
            }
        });
        ////////////////////////////////////////

    }
}