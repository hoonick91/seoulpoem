package com.seoulprojet.seoulpoem.activity;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.MyPageResult;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageSetting extends AppCompatActivity {

    private String userEmail = null;
    private int loginType = 0;

    private static int GALLERY_CODE = 1;
    private int camNum = 1; //1-bg 2-profile

    private ImageButton mypage_setting_back_btn;
    private ImageButton mypage_setting_ok_btn;
    private ImageView mypage_setting_background_img;
    private ImageView mypage_setting_profile_img;
    private EditText mypage_setting_name_et;
    private EditText mypage_setting_message_et;

    private String inform = null;
    private String penName = null;
    private File profile = null;
    private File background = null;

    private  Uri imageUri;

    // network
    NetworkService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page_setting);

        Intent intent = getIntent();
        userEmail = intent.getStringExtra("userEmail");
        loginType = intent.getExtras().getInt("loginType");

        // 객체 초기화
        mypage_setting_back_btn = (ImageButton)findViewById(R.id.mypage_setting_back_btn);
        mypage_setting_ok_btn = (ImageButton)findViewById(R.id.mypage_setting_ok_btn);
        mypage_setting_background_img = (ImageView)findViewById(R.id.mypage_setting_background_img);
        mypage_setting_profile_img = (ImageView)findViewById(R.id.mypage_setting_profile_img);
        mypage_setting_name_et = (EditText)findViewById(R.id.mypage_setting_name_et);
        mypage_setting_message_et = (EditText)findViewById(R.id.mypage_setting_message_et);

        inform = mypage_setting_name_et.getHint().toString();
        penName = mypage_setting_message_et.getHint().toString();

        service = ApplicationController.getInstance().getNetworkService();
        getMyPagePhotos();


        // 이미지 변경
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

        // 페이지 이동
        mypage_setting_back_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), MyPage.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                startActivity(intent);
                finish();
            }
        });
    }

    /****************** mypage 정보 가져오기 *****************/
    public void getMyPagePhotos(){
        final Call<MyPageResult> requestPhoto = service.getMyPage(userEmail, loginType);
        requestPhoto.enqueue(new Callback<MyPageResult>() {
            @Override
            public void onResponse(Call<MyPageResult> call, Response<MyPageResult> response) {
                if(response.isSuccessful()){
                    if(response.body().status.equals("success")){

                        if(response.body().msg.profile == null){
                            mypage_setting_profile_img.setImageResource(R.drawable.profile_tmp);
                        }
                        else{
                            Glide.with(getApplicationContext())
                                    .load(response.body().msg.profile)
                                    .into(mypage_setting_profile_img);
                        }

                        if(response.body().msg.background == null){
                            mypage_setting_background_img.setImageResource(R.drawable.profile_background);
                        }
                        else{
                            Glide.with(getApplicationContext())
                                    .load(response.body().msg.background)
                                    .into(mypage_setting_background_img);

                        }

                        if(!response.body().msg.inform.equals("")){
                            Log.i("inform", "inform : " + response.body().msg.inform);
                            mypage_setting_message_et.setHint(response.body().msg.inform);
                        }
                        mypage_setting_name_et.setHint(response.body().msg.pen_name);
                    }
                }
            }

            @Override
            public void onFailure(Call<MyPageResult> call, Throwable t) {
                Log.i("error", t.getMessage());
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

        ImageButton cam_album_btn = (ImageButton)dialogLayout.findViewById(R.id.cam_album_btn);
        ImageButton cam_default_btn = (ImageButton)dialogLayout.findViewById(R.id.cam_default_btn);
        ImageButton cam_cancel_btn = (ImageButton)dialogLayout.findViewById(R.id.cam_cancel_btn);

        cam_album_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLERY_CODE);
                camDialog.dismiss();
            }
        });

        // default
        cam_default_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Uri fileUri;

                if(camNum == 1){
                    mypage_setting_background_img.setImageResource(R.drawable.profile_background);

                    Log.i("bg default", "bg def" + background);
                    camDialog.dismiss();
                }

                else if(camNum == 2){
                    mypage_setting_profile_img.setImageResource(R.drawable.profile_tmp);

                    Log.i("profile default", "prof def" + profile);
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

    // album
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            try{
                imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                int height = selectedImage.getHeight();
                int width = selectedImage.getWidth();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

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

                //        resized.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                        Log.i("bg album", "bg album" + background);
                    }

                    else if(camNum == 2){
                        mypage_setting_profile_img.setImageBitmap(resized);

                     //   resized.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                        Log.i("profile album", "prof album" + profile);
                    }
                }

                else{
                    if(camNum == 1){
                        mypage_setting_background_img.setImageBitmap(selectedImage);

                        Log.i("bg album", "bg album " + background);
                    }
                    else if(camNum == 2){
                        mypage_setting_profile_img.setImageBitmap(selectedImage);

                        Log.i("profile album", "prof album" + profile);
                    }
                }

            }catch(FileNotFoundException e){
                e.printStackTrace();
                Log.e("CAN'T__FOUND_FILE_ALBUM", e.toString());
            }
        }
        else{
            Log.i("wrong result code", "");
        }
    }

    /********************* post mypage ************************/
    private void postMyPage(){
        RequestBody informBody = RequestBody.create(MediaType.parse("multipart/form-data"),"" + inform);
        RequestBody penNameBody = RequestBody.create(MediaType.parse("multipart/form-data"),"" + penName);

    }

    /**************************이미지 파일 이름 가져오기()******************************************/
    public String getImageNameToUri(Uri data) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);
        return imgName;
    }
}