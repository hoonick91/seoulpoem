package com.seoulprojet.seoulpoem.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bumptech.glide.request.target.ImageViewTargetFactory;
import com.seoulprojet.seoulpoem.R;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MyPageSetting extends AppCompatActivity {

    private static int GALLERY_CODE = 1;
    private int camNum = 1;

    private ImageButton mypage_setting_back_btn;
    private ImageButton mypage_setting_ok_btn;
    private ImageView mypage_setting_background_img;
    private ImageView mypage_setting_profile_img;
    private EditText mypage_setting_name_et;
    private EditText mypage_setting_message_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page_setting);

        //////////////////////////
        mypage_setting_back_btn = (ImageButton)findViewById(R.id.mypage_setting_back_btn);
        mypage_setting_ok_btn = (ImageButton)findViewById(R.id.mypage_setting_ok_btn);
        mypage_setting_background_img = (ImageView)findViewById(R.id.mypage_setting_background_img);
        mypage_setting_profile_img = (ImageView)findViewById(R.id.mypage_setting_profile_img);
        mypage_setting_name_et = (EditText)findViewById(R.id.mypage_setting_name_et);
        mypage_setting_message_et = (EditText)findViewById(R.id.mypage_setting_message_et);
        /////////////////////////////


        /************ 이미지 변경 *************/
        mypage_setting_background_img.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                camNum = 1;
                showCamDialog();
            }
        });

        mypage_setting_profile_img.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                camNum = 2;
                showCamDialog();
            }
        });

        //////////페이지 이동/////////
        mypage_setting_back_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), MyPage.class);
                startActivity(intent);
                finish();
            }
        });
    }


    /******************cam dailog******************/
    private void showCamDialog(){

        LayoutInflater dialog = LayoutInflater.from(this);
        final View dialogLayout = dialog.inflate(R.layout.mypage_setting_dialog, null);
        final Dialog camDialog = new Dialog(this);

        camDialog.setContentView(dialogLayout);
        camDialog.show();

        Button cam_album_btn = (Button)dialogLayout.findViewById(R.id.cam_album_btn);
        Button cam_default_btn = (Button)dialogLayout.findViewById(R.id.cam_default_btn);
        Button cam_cancel_btn = (Button)dialogLayout.findViewById(R.id.cam_cancel_btn);

        cam_album_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLERY_CODE);
                camDialog.dismiss();
            }
        });

        cam_default_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(camNum == 1){
                    mypage_setting_background_img.setImageBitmap(null);
                    camDialog.dismiss();
                }

                else if(camNum == 2){
                    mypage_setting_profile_img.setImageResource(R.drawable.profile_tmp);
                    camDialog.dismiss();
                }
            }
        });

        cam_cancel_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                camDialog.cancel();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            try{
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                int height = selectedImage.getHeight();
                int width = selectedImage.getWidth();

                // 이미지 비율
                    /*
                    if(height == width){
                        mypage_setting_background_img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }

                    else if(height > width){
                        mypage_setting_background_img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }

                    else{
                        mypage_setting_background_img.setScaleType(ImageView.ScaleType.FIT_XY);
                    }*/


                // resize 및 imageview 지정
                if(width > 1080){
                    Bitmap resized = null;

                    while(width > 1080){
                        resized = Bitmap.createScaledBitmap(selectedImage, 1080, (height * 1080) / width, true);
                        height = resized.getHeight();
                        width = resized.getWidth();
                    }

                    if(camNum == 1){
                        mypage_setting_background_img.setImageBitmap(resized);
                    }

                    else if(camNum == 2){
                        mypage_setting_profile_img.setImageBitmap(resized);
                    }
                }

                else{
                    if(camNum == 1){
                        mypage_setting_background_img.setImageBitmap(selectedImage);
                    }
                    else if(camNum == 2){
                        mypage_setting_profile_img.setImageBitmap(selectedImage);
                    }
                }

            }catch(FileNotFoundException e){
                e.printStackTrace();
                Log.e("CAN'T__FOUND_FILE_ALBUM", e.toString());
            }
        }
        else{
            Toast.makeText(this, "wrong result code", Toast.LENGTH_LONG);
        }
    }
}