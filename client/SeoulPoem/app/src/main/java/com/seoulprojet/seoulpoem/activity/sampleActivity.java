package com.seoulprojet.seoulpoem.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.seoulprojet.seoulpoem.R;

/**
 * Created by minjeong on 2017-09-27.
 */

public class sampleActivity extends AppCompatActivity {

    Button sample_btn;
    int USING_CAMERA = 1;
    int USING_GALLARY = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        sample_btn = (Button)findViewById(R.id.sample_btn);

        sample_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
                /*Intent intent = new Intent(getApplicationContext(), WritePoemActivity.class);
                startActivity(intent);*/
            }
        });

    }

    public void dialog(){
        final CharSequence[] items = {"카메라", "갤러리"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);     // 여기서 this는 Activity의 this

        // 여기서 부터는 알림창의 속성 설정
        builder.setTitle("")        // 제목 설정
                .setItems(items, new DialogInterface.OnClickListener(){    // 목록 클릭시 설정
                    public void onClick(DialogInterface dialog, int index){
                        if(index == 0) { // 카메라 클릭시
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent,USING_CAMERA);

                        }else{ //갤러리 클릭시
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                            intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, USING_GALLARY);
                        }
                    }
                });

        AlertDialog dialog = builder.create();    // 알림창 객체 생성
        dialog.show();    // 알림창 띄우기
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == USING_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                try {

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    


                } catch (Exception e) {
                    Log.e("test", e.getMessage());
                }
            }
        }
        else if(requestCode == USING_GALLARY){
            if (resultCode == Activity.RESULT_OK) {
                try {

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());


                } catch (Exception e) {
                    Log.e("test", e.getMessage());
                }
            }
        }
    }
}
