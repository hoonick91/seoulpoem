package com.seoulprojet.seoulpoem.activity;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.seoulprojet.seoulpoem.R;

public class ReadingPoem extends AppCompatActivity {

    TextView poem_title;
    ImageView poem_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_poem);
        setId();
    }

    private void setId(){
        poem_title = (TextView)findViewById(R.id.poem_title);
        poem_img = (ImageView)findViewById(R.id.poem_img);
    }
}
