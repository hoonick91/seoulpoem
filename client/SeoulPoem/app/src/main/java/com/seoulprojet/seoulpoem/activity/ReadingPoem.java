package com.seoulprojet.seoulpoem.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.seoulprojet.seoulpoem.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class ReadingPoem extends AppCompatActivity {

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
