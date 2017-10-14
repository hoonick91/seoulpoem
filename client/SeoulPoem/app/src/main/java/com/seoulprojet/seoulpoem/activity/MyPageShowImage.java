package com.seoulprojet.seoulpoem.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.MyPageResult;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageShowImage extends AppCompatActivity {

    private ImageButton image_back_btn;
    private ImageView showImage, imgView;
    private String status = "";

    // network
    private ArrayList<MyPageResult> myPageResults;
    NetworkService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page_show_image);

        service = ApplicationController.getInstance().getNetworkService();

        Intent intent = getIntent();
        status = intent.getStringExtra("status");

        image_back_btn = (ImageButton)findViewById(R.id.mypage_show_back_btn);
        showImage = (ImageView)findViewById(R.id.mypage_show_image);

        getMyPagePhotos();

        image_back_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /*************** 이미지 가져오기 *******************/
    public void getMyPagePhotos(){
        final Call<MyPageResult> requestPhoto = service.getMyPage("godz33@naver.com", 1);
        requestPhoto.enqueue(new Callback<MyPageResult>() {
            @Override
            public void onResponse(Call<MyPageResult> call, Response<MyPageResult> response) {
                if(response.isSuccessful()){
                    if(response.body().status.equals("success")){

                        if(status.equals("background")){
                            Log.i("background img", "");
                            Glide.with(getApplicationContext())
                                    .load(response.body().msg.background)
                                    .into(showImage);
                        }
                        else if(status.equals("profile")){
                            Log.i("profile img", "");
                            Glide.with(getApplicationContext())
                                    .load(response.body().msg.profile)
                                    .into(showImage);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MyPageResult> call, Throwable t) {
                Log.i("error", t.getMessage());
            }
        });
    }
}
