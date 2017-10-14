package com.seoulprojet.seoulpoem.activity;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.component.Preview;

import static android.R.attr.id;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

/**
 * Created by minjeong on 2017-09-27.
 */

public class PreviewAcitivity extends AppCompatActivity{

    Button back; //뒤로가기 버튼
    TextView preview_title; //제목
    TextView preview_content; //내용
    ImageView preview_img; //이미지파일
    LinearLayout paper_background; //종이질감
    Button complete; //확인버튼

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        setId();
        click(); //클릭리스너 등록
        setItem(); //내용 가져오기

    }
    private void setId(){
        back = (Button)findViewById(R.id.back);
        preview_title = (TextView)findViewById(R.id.preview_title);
        preview_content = (TextView)findViewById(R.id.preview_content);
        preview_img = (ImageView) findViewById(R.id.preview_img);
        paper_background = (LinearLayout)findViewById(R.id.paper_background);
        complete = (Button)findViewById(R.id.complete);

    }
    private void click(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //서버에 전송하기 사진, 제목, 내용.종이질감.글자색,글자설정 등
            }
        });
    }
    protected void setItem(){

        preview_img.setImageURI(Preview.photo);
        preview_title.setText(Preview.title);
        preview_content.setText(Preview.content);
        preview_content.setTextSize(Preview.font_size);
        Log.e("폰트사이즈 : ",""+Preview.font_size);
        preview_content.setTextColor(Preview.color);
        if(Preview.sortinfo == 1)
            preview_content.setGravity(Gravity.LEFT);
        else if(Preview.sortinfo == 2)
            preview_content.setGravity(Gravity.RIGHT);
        else if(Preview.sortinfo == 3)
            preview_content.setGravity(Gravity.CENTER_HORIZONTAL);
        else
            preview_content.setGravity(Gravity.NO_GRAVITY);


        if(Preview.bold == 1){
            preview_content.setTypeface(null, Typeface.BOLD);
            if (Preview.inclination == 1){
                preview_content.setTypeface(null, Typeface.BOLD_ITALIC);
                if(Preview.underline == 1) {
                    preview_content.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                }
                else{
                    preview_content.setPaintFlags(0);
                }
            }else{}
        }else{
        }
        int[] paper = new int[]{R.drawable.paper1,R.drawable.paper2,R.drawable.paper3,R.drawable.paper4};
        paper_background.setBackgroundResource(paper[Preview.background-1]);

    }


}
