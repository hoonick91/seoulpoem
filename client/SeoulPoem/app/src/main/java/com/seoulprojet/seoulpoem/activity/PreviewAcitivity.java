package com.seoulprojet.seoulpoem.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.component.Preview;
import com.seoulprojet.seoulpoem.model.SavePoemResult;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by minjeong on 2017-09-27.
 */

public class PreviewAcitivity extends AppCompatActivity{

    //네트워킹
    NetworkService service;

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
        initNetwork(); // 네트워크 초기화
        setId();
        click(); //클릭리스너 등록
        setItem(); //내용 가져오기


    }
    /****************************************네트워크 초기화*****************************************/
    private void initNetwork() {
        service = ApplicationController.getInstance().getNetworkService();
    }

    /****************************************아이디값 연결*****************************************/
    private void setId(){
        back = (Button)findViewById(R.id.back);
        preview_title = (TextView)findViewById(R.id.preview_title);
        preview_content = (TextView)findViewById(R.id.preview_content);
        preview_img = (ImageView) findViewById(R.id.preview_img);
        paper_background = (LinearLayout)findViewById(R.id.paper_background);
        complete = (Button)findViewById(R.id.complete);

    }

    /************************************클릭 리스너 연결*****************************************/
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
                savePoem();

            }
        });
    }

    /*******************************뷰들 값 가져오기****************************************/
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

    /*****************************서버 통신 준비 단계****************************************/
    private void savePoem(){

        RequestBody title = RequestBody.create(MediaType.parse("multipart/form-data"), Preview.title);
        RequestBody font_type = RequestBody.create(MediaType.parse("multipart/form-data"), "0");
        RequestBody font_size = RequestBody.create(MediaType.parse("multipart/form-data"), ""+Preview.font_size);
        RequestBody bold = RequestBody.create(MediaType.parse("multipart/form-data"), ""+Preview.bold);
        RequestBody inclination = RequestBody.create(MediaType.parse("multipart/form-data"), ""+Preview.inclination);
        RequestBody underline = RequestBody.create(MediaType.parse("multipart/form-data"), ""+Preview.underline);
        RequestBody color = RequestBody.create(MediaType.parse("multipart/form-data"), ""+Preview.color);
        RequestBody sortinfo = RequestBody.create(MediaType.parse("multipart/form-data"), ""+Preview.sortinfo);
        RequestBody content = RequestBody.create(MediaType.parse("multipart/form-data"), Preview.content);
        RequestBody tags = RequestBody.create(MediaType.parse("multipart/form-data"), Preview.tags);
        RequestBody inform = RequestBody.create(MediaType.parse("multipart/form-data"), Preview.inform);
        RequestBody background = RequestBody.create(MediaType.parse("multipart/form-data"), ""+Preview.background);

        MultipartBody.Part photo = null;

        if (Preview.photo_location == null) {
            photo = null;
        } else {

            //resizing
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2; //얼마나 줄일지 설정하는 옵션 4--> 1/4로 줄이겠다

            InputStream in = null; // here, you need to get your context.
            try {
                in = getContentResolver().openInputStream(Preview.photo);

            } catch (FileNotFoundException e) {
                Log.e("error!!!!","");
                e.printStackTrace();
            }

            Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);


            RequestBody photoBody = RequestBody.create(MediaType.parse("image/*"), baos.toByteArray());

            // MultipartBody.Part 실제 파일의 이름을 보내기 위해 사용!!
            photo = MultipartBody.Part.createFormData("photo", Preview.photoName, photoBody);

       /*     File file = new File(Preview.photo_location);
            RequestBody fileBody = RequestBody.create(MediaType.parse("image*//*"), file);
            photo = MultipartBody.Part.createFormData("photo", file.getName(), fileBody);*/

            Log.e("photofile",Preview.photo_location);
            Log.e("photoname",Preview.photoName);
            Log.e("photo uri",""+Preview.photo);
        }

        /****************************************서버에 정보 보냄**************************************/
        Call<SavePoemResult> request = service.savePoem("godz33@naver.com",1,
                photo,title,font_size,bold,inclination,underline,
                color,sortinfo,content,tags,inform,background);
        request.enqueue(new Callback<SavePoemResult>() {
            @Override
            public void onResponse(Call<SavePoemResult> call, Response<SavePoemResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().status.equals("success")) {
                        Toast.makeText(PreviewAcitivity.this, "등록이 완료되었습니다!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PreviewAcitivity.this,ReadingPoemActivity.class);
                        intent.putExtra("articles_id",response.body().articles_id);
                        startActivity(intent);
                    } else {

                    }
                } else {
                    Log.e("Posterr",response.message());
                }
            }

            @Override
            public void onFailure(Call<SavePoemResult> call, Throwable t) {

            }
        });

    }

    /**************************이미지 파일 이름 가져오기******************************************/

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
