package com.seoulprojet.seoulpoem.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.seoulprojet.seoulpoem.R;

public class MyPageShowImage extends AppCompatActivity {

    private ImageButton image_back_btn;
    private ImageView showImage, imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page_show_image);

        Intent intent = getIntent();
        final String status = intent.getStringExtra("status");

        image_back_btn = (ImageButton)findViewById(R.id.mypage_show_back_btn);
        showImage = (ImageView)findViewById(R.id.mypage_show_image);

        // 서버 연동 후 서버에서 받아오도록 변경하기 !
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_my_page, null);

        if(status.equals("profile")){
            imgView = (ImageView)view.findViewById(R.id.mypage_profile_img);
        }

        else{
            imgView = (ImageView)view.findViewById(R.id.mypage_bg_iv);
        }

        BitmapDrawable bitmapDrawable = (BitmapDrawable)imgView.getDrawable();
        Bitmap tmpBitmap = bitmapDrawable.getBitmap();
        showImage.setImageBitmap(tmpBitmap);

        image_back_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
