package com.seoulprojet.seoulpoem.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    int articles_id;

    TextView poem_title;
    ImageView poem_img;
    ImageButton poem_path;
    RelativeLayout title_layout;

    boolean openPoem = false;
    String photo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_poem);

        String temp = getIntent().getStringExtra("articles_id");
        articles_id  = Integer.parseInt(temp);
        setId();
        setClick();
        initNetwork();
        getInfo();
    }

    private void setId(){
        poem_title = (TextView)findViewById(R.id.poem_title);
        poem_img = (ImageView)findViewById(R.id.poem_img);
        poem_path = (ImageButton)findViewById(R.id.poem_path);
        title_layout = (RelativeLayout)findViewById(R.id.title_layout);

    }

    //버튼 클릭 모션
    private void setClick(){
        poem_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!openPoem) {
                    ViewGroup.LayoutParams params = poem_img.getLayoutParams();
                    params.height = poem_img.getHeight() / 4;
                    poem_img.setLayoutParams(params);
                    poem_img.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) title_layout.getLayoutParams();
                    params2.topMargin = title_layout.getTop() / 3;
                    title_layout.setLayoutParams(params2);

                    poem_title.setTextSize(18);
                    poem_path.setImageResource(R.drawable.path3);
                    openPoem = true;
                }else{
                    ViewGroup.LayoutParams params = poem_img.getLayoutParams();
                    params.height = poem_img.getHeight() * 4;
                    poem_img.setLayoutParams(params);
                    poem_img.setScaleType(ImageView.ScaleType.FIT_CENTER);

                    RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) title_layout.getLayoutParams();
                    params2.topMargin = title_layout.getTop() * 3;
                    title_layout.setLayoutParams(params2);

                    poem_title.setTextSize(30);
                    poem_path.setImageResource(R.drawable.path2);
                    openPoem = false;
                }



            /*    Bitmap bitmap=null;
                Drawable drawable = poem_img.getDrawable();
                if (drawable instanceof GlideBitmapDrawable) {
                    bitmap = ((GlideBitmapDrawable) drawable).getBitmap();
                } else if (drawable instanceof TransitionDrawable) {
                    TransitionDrawable transitionDrawable = (TransitionDrawable) drawable;
                    int length = transitionDrawable.getNumberOfLayers();
                    for (int i = 0; i < length; ++i) {
                        Drawable child = transitionDrawable.getDrawable(i);
                        if (child instanceof GlideBitmapDrawable) {
                            bitmap = ((GlideBitmapDrawable) child).getBitmap();
                            break;
                        } else if (child instanceof SquaringDrawable
                                && child.getCurrent() instanceof GlideBitmapDrawable) {
                            bitmap = ((GlideBitmapDrawable) child.getCurrent()).getBitmap();
                            break;
                        }
                    }
                } else if (drawable instanceof SquaringDrawable) {
                    bitmap = ((GlideBitmapDrawable) drawable.getCurrent()).getBitmap();
                }
                int width=(int)(getWindowManager().getDefaultDisplay().getWidth()); // 가로 사이즈 지정
                Log.e("size_width",""+width);
                int height=(int)(getWindowManager().getDefaultDisplay().getHeight() ); // 세로 사이즈 지정
                Log.e("size_height",""+height);
                Bitmap resizedbitmap=Bitmap.createScaledBitmap(bitmap, width, height, true); // 이미지 사이즈 조정
                poem_img.setImageBitmap(resizedbitmap); // 이미지뷰에 조정한 이미지 넣기*/

            }
        });
    }

    /****************************************네트워크 초기화*****************************************/
    private void initNetwork() {
        service = ApplicationController.getInstance().getNetworkService();
    }

    /****************************************서버통신 정보 받음**************************************/
    private void getInfo(){

        Call<ReadingPoem> request = service.readPoem("godz33@naver.com",1, articles_id);
        request.enqueue(new Callback<ReadingPoem>() {
            @Override
            public void onResponse(Call<ReadingPoem> call, Response<ReadingPoem> response) {
                if (response.isSuccessful()) {
                    poem_title.setText(response.body().article.title);
                    photo = response.body().article.photo;
                    Glide.with(ReadingPoemActivity.this)
                            .load(photo)
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


}
