package com.seoulprojet.seoulpoem.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.seoulprojet.seoulpoem.R;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyPage extends AppCompatActivity {

    private ImageView mypage_profile_img;
    private Button mypage_hamburger_btn;
    private Button mypage_setting_btn;
    private TextView mypage_name_txt;
    private TextView mypage_message_txt;
    private Button mypage_upload_btn;
    private Button mypage_photo_btn;
    private Button mypage_poem_btn;

    View drawerView;
    DrawerLayout drawerLayout;
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


        ///////////////////////////drawer
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerView = (View)findViewById(R.id.drawer);
        mypage_hamburger_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
                Toast.makeText(getApplicationContext(), "drawer open", Toast.LENGTH_LONG);
            }
        });

        /////// 페이지 이동 //////
        mypage_setting_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyPageSetting.class);
                startActivity(intent);
             }
        });

        /////////fragment////////

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.mypage_fragment, new MyPagePhotoFragment())
                .commit();

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