package com.seoulprojet.seoulpoem.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.ReadingPoem;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReadingPoemActivity extends AppCompatActivity {

    //네트워킹
    NetworkService service;
    String articles_id;

    TextView poem_title;
    ImageView poem_img;
    ImageButton poem_path;
    SlidingUpPanelLayout sliding_layout;
    RelativeLayout dragView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_poem);
        setId();
        setClick();
        initNetwork();
        getInfo();
    }

    private void setId(){
        poem_title = (TextView)findViewById(R.id.poem_title);
        poem_img = (ImageView)findViewById(R.id.poem_img);
        poem_path = (ImageButton)findViewById(R.id.poem_path);
        sliding_layout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        dragView = (RelativeLayout)findViewById(R.id.dragView);

    }

    private void setClick(){
        sliding_layout.setPanelSlideListener(onSlideListener());
        poem_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //슬라이딩바 올라오기
                sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

    }
    /****************************************네트워크 초기화*****************************************/
    private void initNetwork() {
        service = ApplicationController.getInstance().getNetworkService();

        Intent intent = getIntent();
        articles_id  = intent.getStringExtra("articles_id");
    }

    /****************************************서버통신 정보 받음**************************************/
    private void getInfo(){

        Call<ReadingPoem> request = service.readPoem("godz33@naver.com",1, Integer.parseInt(articles_id));
        request.enqueue(new Callback<ReadingPoem>() {
            @Override
            public void onResponse(Call<ReadingPoem> call, Response<ReadingPoem> response) {
                if (response.isSuccessful()) {
                    poem_title.setText(response.body().article.title);
                    Glide.with(ReadingPoemActivity.this)
                            .load(response.body().article.photo)
                            .into(poem_img);


                } else {
                    Log.e("err",response.message());
                }
            }

            @Override
            public void onFailure(Call<ReadingPoem> call, Throwable t) {

            }
        });

    }

    /*******************************슬라이딩 모션별 작업*******************************************/
    private SlidingUpPanelLayout.PanelSlideListener onSlideListener() {
        return new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                Toast.makeText(getApplicationContext(),"onPanelSlide",Toast.LENGTH_LONG);
                //움직이는 중
                if(sliding_layout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED){
                   // dragView.setLayoutParams(new RelativeLayout(.LayoutParams(360,)));
                }
            }

            @Override
            public void onPanelCollapsed(View view) {
                //sliding꺼질때
                Toast.makeText(getApplicationContext(),"onPanelCollapsed",Toast.LENGTH_LONG);
            }

            @Override
            public void onPanelExpanded(View view) {
                //sliding 올라올때
                Toast.makeText(getApplicationContext(),"onPanelExpanded",Toast.LENGTH_LONG);
            }

            @Override
            public void onPanelAnchored(View view) {
                //  textView.setText("panel anchored");
                Toast.makeText(getApplicationContext(),"onPanelAnchored",Toast.LENGTH_LONG);

            }

            @Override
            public void onPanelHidden(View view) {
                //  textView.setText("panel is Hidden");
                Toast.makeText(getApplicationContext(),"onPanelHidden",Toast.LENGTH_LONG);

            }
        };
    }
}
