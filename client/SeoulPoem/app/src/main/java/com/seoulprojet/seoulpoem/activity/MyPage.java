package com.seoulprojet.seoulpoem.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.MyPagePoemResult;
import com.seoulprojet.seoulpoem.model.MyPageResult;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPage extends AppCompatActivity {

    private ImageView mypage_profile_img;
    private ImageButton mypage_hamburger_btn;
    private ImageButton mypage_setting_btn;
    private TextView mypage_name_txt;
    private TextView mypage_message_txt;
    private ImageButton mypage_upload_btn;
    private ImageButton mypage_photo_btn;
    private ImageView mypage_bg_iv;
    private ImageButton mypage_poem_btn;

    // drawer
    private ImageButton hamburger_setting_btn, hamburger_mypage_btn, hamburger_scrab_btn, hamburger_today_btn, hamburger_writer_btn,hamburger_notice_btn;
    private TextView hamburger_name, hamburger_message;
    private ImageView hamburger_profile, hamburger_bg;
    private View drawerView;
    private DrawerLayout drawerLayout;

    // network
    NetworkService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        // 서비스 객체 초기화
        service = ApplicationController.getInstance().getNetworkService();

        // network
        getMypage();
        getMenuMypage();

        // find view
        mypage_profile_img = (ImageView)findViewById(R.id.mypage_profile_img);
        mypage_hamburger_btn = (ImageButton)findViewById(R.id.mypage_hamburger_btn);
        mypage_setting_btn = (ImageButton)findViewById(R.id.mypage_setting_btn);
        mypage_name_txt = (TextView)findViewById(R.id.mypage_name_txt);
        mypage_message_txt = (TextView)findViewById(R.id.mypage_message_txt);
        mypage_upload_btn = (ImageButton)findViewById(R.id.mypage_upload_btn);
        mypage_photo_btn = (ImageButton)findViewById(R.id.mypage_photo_btn);
        mypage_poem_btn = (ImageButton)findViewById(R.id.mypage_poem_btn);
        mypage_bg_iv = (ImageView)findViewById(R.id.mypage_bg_iv);

        // drawer
        showHamburger();


        // 페이지 이동
        mypage_setting_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyPageSetting.class);
                startActivity(intent);
             }
        });


        // fragment
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.mypage_fragment, new MyPagePhotoFragment())
                .commit();

        mypage_photo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mypage_poem_btn.setImageResource(R.drawable.mypage_poem_un_btn);
                mypage_photo_btn.setImageResource(R.drawable.mypage_photo_on_btn);
               getFragmentManager()
                       .beginTransaction()
                       .replace(R.id.mypage_fragment, new MyPagePhotoFragment())
                       .commit();
            }
        });

        mypage_poem_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                mypage_poem_btn.setImageResource(R.drawable.mypage_poem_on_btn);
                mypage_photo_btn.setImageResource(R.drawable.mypage_photo_un);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mypage_fragment, new MyPagePoemFragment())
                        .commit();
            }
        });


        // 이미지 디테일

        mypage_profile_img.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyPageShowImage.class);
                intent.putExtra("status", "profile");
                startActivity(intent);
            }
        });

        mypage_bg_iv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyPageShowImage.class);
                intent.putExtra("status", "background");
                startActivity(intent);
            }
        });

    }

    /******************* mypage 정보 가져오기 ******************/
    public void getMypage(){
        Call<MyPageResult> requestMyPage = service.getMyPage("godz33@naver.com", 1);

        requestMyPage.enqueue(new Callback<MyPageResult>() {
            @Override
            public void onResponse(Call<MyPageResult> call, Response<MyPageResult> response) {
                if(response.isSuccessful()){
                    Log.d("error", "xxx");
                    if(response.body().status.equals("success")){
                        mypage_name_txt.setText(response.body().msg.pen_name);
                        mypage_message_txt.setText(response.body().msg.inform);
                        Glide.with(getApplicationContext())
                                .load(response.body().msg.profile)
                                .into(mypage_profile_img);
                        Glide.with(getApplicationContext())
                                .load(response.body().msg.background)
                                .into(mypage_bg_iv);
                    }
                }
            }

            @Override
            public void onFailure(Call<MyPageResult> call, Throwable t) {
                Log.i("mypage error", t.getMessage());
            }
        });
    }

    /********* drawer **********/
    public void showHamburger(){

        hamburger_mypage_btn = (ImageButton)findViewById(R.id.hamburger_mypage_btn);
        hamburger_scrab_btn = (ImageButton)findViewById(R.id.hamburger_scrab_btn);
        hamburger_today_btn = (ImageButton)findViewById(R.id.hamburger_todayseoul_btn);
        hamburger_writer_btn = (ImageButton)findViewById(R.id.hamburger_writerlist_btn);
        hamburger_notice_btn = (ImageButton)findViewById(R.id.hamburger_notice_btn);
        hamburger_setting_btn = (ImageButton)findViewById(R.id.hamburger_setting_btn);
        hamburger_name = (TextView)findViewById(R.id.hamburger_name);
        hamburger_message = (TextView)findViewById(R.id.hamburger_message);
        hamburger_profile = (ImageView)findViewById(R.id.hamburger_profile_img);
        hamburger_bg = (ImageView)findViewById(R.id.hamburger_bg);

        drawerLayout = (DrawerLayout)findViewById(R.id.mypage_drawer_layout);
        drawerView = (View)findViewById(R.id.drawer);
        mypage_hamburger_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });

        hamburger_mypage_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyPage.class);
                startActivity(intent);
            }
        });

        hamburger_today_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TodaySeoul.class);
                startActivity(intent);
                finish();
            }
        });

        hamburger_setting_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingPage.class);
                startActivity(intent);
                finish();
            }
        });

        hamburger_notice_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                Intent intent = new Intent(getApplicationContext(), Notice.class);
                startActivity(intent);
                finish();
            }
        });

        hamburger_writer_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WriterList.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /******************* mypage 정보 가져오기 ******************/
    public void getMenuMypage(){
        Call<MyPageResult> requestMyPage = service.getMyPage("godz33@naver.com", 1);

        requestMyPage.enqueue(new Callback<MyPageResult>() {
            @Override
            public void onResponse(Call<MyPageResult> call, Response<MyPageResult> response) {
                if(response.isSuccessful()){
                    Log.d("error", "xxx");
                    if(response.body().status.equals("success")){
                        hamburger_name.setText(response.body().msg.pen_name);
                        hamburger_message.setText(response.body().msg.inform);
                        Glide.with(getApplicationContext())
                                .load(response.body().msg.profile)
                                .into(hamburger_profile);
                        Glide.with(getApplicationContext())
                                .load(response.body().msg.background)
                                .into(hamburger_bg);
                    }
                }
            }

            @Override
            public void onFailure(Call<MyPageResult> call, Throwable t) {
                Log.i("mypage error", t.getMessage());
            }
        });
    }
}