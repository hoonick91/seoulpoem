package com.seoulprojet.seoulpoem.activity;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.seoulprojet.seoulpoem.R;

public class MainActivity extends AppCompatActivity {

    private ImageView ivHamberger, ivPictures, ivSearch;
    private ViewPager vpPoems;
    private ImageView img01, img02, img03, img04, img05;
    private ImageButton ibUpload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //findView
        findView();


    }



    /***************************************findView***********************************************/
    public void findView(){
        ivHamberger = (ImageView) findViewById(R.id.ivHamberger);
        ivPictures = (ImageView) findViewById(R.id.ivPictures);
        ivSearch = (ImageView) findViewById(R.id.ivSearch);
        vpPoems = (ViewPager) findViewById(R.id.vp);
        img01 = (ImageView) findViewById(R.id.iv01);
        img02 = (ImageView) findViewById(R.id.iv02);
        img03 = (ImageView) findViewById(R.id.iv03);
        img04 = (ImageView) findViewById(R.id.iv04);
        img05 = (ImageView) findViewById(R.id.iv05);
        ibUpload = (ImageButton) findViewById(R.id.ibUpload);
    }




}
