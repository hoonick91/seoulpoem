package com.seoulprojet.seoulpoem.activity;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.ReadingPoem;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.graphics.Typeface.BOLD;
import static android.graphics.Typeface.BOLD_ITALIC;
import static android.graphics.Typeface.ITALIC;
import static android.graphics.Typeface.NORMAL;

public class ReadingPoemActivity extends AppCompatActivity {

    //네트워킹
    NetworkService service;
    int articles_id;

    TextView poem_title;
    TextView poem_content;
    ImageView poem_img;
    ImageButton poem_path;
    ImageButton download;
    ImageButton another;
    RelativeLayout title_layout;
    TextView writer_name;
    TextView poem_tags;
    CircleImageView writer_img;
    LinearLayout reading_background;
    RecyclerView recyclerPoem;
    MyRecyclerAdapter recyclerAdapter;

    boolean openPoem = false;
    boolean style_check[];
    String photo;
    ArrayList<ReadingPoem.Otherinfo> another_photo;

    //유저 정보
    private String userEmail = null;
    private int loginType = 0;

    //다이얼로그
    private AddWorkDialog addWorkDialog;
    private SettingDialog settingDialog;
    private SettingDialog02 settingDialog02;
    private InfoDialog infoDialog;

    //작품담기 다이얼로그 리스너
    private View.OnClickListener addDialog_leftListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(ReadingPoemActivity.this, AddActivity.class);
            intent.putExtra("userEmail", userEmail);
            intent.putExtra("loginType", loginType);
            startActivity(intent);
            addWorkDialog.dismiss();
        }
    };
    private View.OnClickListener addDialog_rightListener = new View.OnClickListener() {
        public void onClick(View v) {
            addWorkDialog.dismiss();
        }
    };

    //설정 다이얼로그 리스너
    private View.OnClickListener settingDialog_listener02 = new View.OnClickListener() {
        public void onClick(View v) {
            //원래 뜬 다이얼로그 없애고
            settingDialog02.dismiss();

            infoDialog = new InfoDialog(ReadingPoemActivity.this);
            infoDialog.setCanceledOnTouchOutside(true);
            infoDialog.show();
        }
    };

    private View.OnClickListener settingDialog_listener03 = new View.OnClickListener() {
        public void onClick(View v) {
            Toast.makeText(ReadingPoemActivity.this, "333", Toast.LENGTH_SHORT).show();
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_poem);


        //유저 정보 가져오기
        Intent intent = getIntent();
        userEmail = intent.getExtras().getString("userEmail");
        loginType = intent.getExtras().getInt("loginType");

        String temp = getIntent().getStringExtra("articles_id");
        articles_id  = Integer.parseInt(temp);
        setId();
        setRecycularView();
        setClick();
        initNetwork();
        getInfo();

    }

    private void setId(){
        poem_title = (TextView)findViewById(R.id.poem_title);
        poem_content = (TextView)findViewById(R.id.poem_content);
        poem_img = (ImageView)findViewById(R.id.poem_img);
        poem_path = (ImageButton)findViewById(R.id.poem_path);
        another = (ImageButton)findViewById(R.id.another);
        title_layout = (RelativeLayout)findViewById(R.id.title_layout);
        writer_name = (TextView)findViewById(R.id.writer_name);
        poem_tags = (TextView)findViewById(R.id.poem_tags);
        writer_img = (CircleImageView)findViewById(R.id.writer_img);
        reading_background = (LinearLayout)findViewById(R.id.reading_background);
        recyclerPoem = (RecyclerView)findViewById(R.id.recyclerPoem);
        download = (ImageButton)findViewById(R.id.download);

        style_check = new boolean[]{false,false,false};

    }

    //버튼 클릭 모션
    private void setClick(){
        poem_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!openPoem) { //세로긴화면 -> 가로긴화면으로 축소시
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
                }else{  //세로긴화면 -> 가로긴화면으로 축소시
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

            }
        });
        another.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //작품을 쓴 사람이 현재 사용자라면
//                settingDialog = new SettingDialog(DetailActivity.this,
//                        "상세정보",
//                        "수정하기",
//                        settingDialog_listener02,
//                        settingDialog_listener03);
//                settingDialog.setCanceledOnTouchOutside(true);
//                settingDialog.show();

                //작품을 쓴 쓴 사람이 현재 사용자가 아니라면
                settingDialog02 = new SettingDialog02(ReadingPoemActivity.this,
                        "상세정보",
                        settingDialog_listener02);
                settingDialog02.setCanceledOnTouchOutside(true);
                settingDialog02.show();

            }
        });


        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWorkDialog = new AddWorkDialog(ReadingPoemActivity.this,
                        "#작품 담기",
                        "작품 담기에 작품을 담았습니다.",
                        "작품 담기로 이동하시겠습니까?",
                        addDialog_leftListener,
                        addDialog_rightListener);
                addWorkDialog.setCanceledOnTouchOutside(true);

                addWorkDialog.show();
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
                    poem_content.setText(response.body().article.content);

                    photo = response.body().article.photo;
                    Glide.with(ReadingPoemActivity.this)
                            .load(photo)
                            .into(poem_img);
                    Glide.with(ReadingPoemActivity.this)
                            .load(response.body().article.user.profile)
                            .into(writer_img);
                    poem_tags.setText(response.body().article.tags);
                    writer_name.setText(response.body().article.user.pen_name);
                    if(response.body().article.background.equals("4"))
                        reading_background.setBackgroundResource(R.drawable.paper4);
                    else if(response.body().article.background.equals("3"))
                        reading_background.setBackgroundResource(R.drawable.paper3);
                    else if(response.body().article.background.equals("2"))
                        reading_background.setBackgroundResource(R.drawable.paper2);
                    else
                        reading_background.setBackgroundResource(R.drawable.paper1);

                    if (response.body().article.setting.bold == 1){
                        if( response.body().article.setting.inclination == 1){
                            poem_content.setTypeface(null, BOLD_ITALIC);
                            style_check[0] = true;
                            style_check[1] = true;

                        }else {
                            style_check[0] = true;
                            poem_content.setTypeface(null, BOLD);
                        }
                    }else{
                        if( response.body().article.setting.inclination == 1){
                            style_check[1] = true;
                            poem_content.setTypeface(null, ITALIC);
                        }else {
                            poem_content.setTypeface(null, NORMAL);
                        }
                    };

                    if(response.body().article.setting.underline == 1){
                        style_check[2] = true;
                        poem_content.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                    }
                    else {
                        poem_content.setPaintFlags(0);
                    }

                    if(response.body().article.setting.color == 1)
                        poem_content.setTextColor(Color.parseColor("#ffffff"));
                    else if(response.body().article.setting.color == 2)
                        poem_content.setTextColor(Color.parseColor("#773511"));
                    else  if(response.body().article.setting.color == 3)
                        poem_content.setTextColor(Color.parseColor("#765745"));
                    else  if(response.body().article.setting.color == 4)
                        poem_content.setTextColor(Color.parseColor("#888888"));
                    else
                        poem_content.setTextColor(Color.parseColor("#000000"));

                    if(response.body().article.setting.sort==1){
                        poem_content.setGravity(Gravity.LEFT);
                    }else if(response.body().article.setting.sort==2){
                        poem_content.setGravity(Gravity.RIGHT);
                    }else if(response.body().article.setting.sort ==3){
                        poem_content.setGravity(Gravity.CENTER_HORIZONTAL);
                    }else{
                        poem_content.setGravity(Gravity.NO_GRAVITY);
                    };

                        another_photo = response.body().article.user.others;
                    recyclerAdapter.setAdapter(another_photo);
                    recyclerAdapter.notifyDataSetChanged();



                } else {
                    Log.e("err",response.message());
                }
            }

            @Override
            public void onFailure(Call<ReadingPoem> call, Throwable t) {

            }
        });

    }
    /**********************리사이클러 뷰 셋팅작업  여기서부터 끝까지 ******************************/
    private void setRecycularView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerPoem.setLayoutManager(layoutManager);

        another_photo = new ArrayList<>();
        recyclerAdapter = new MyRecyclerAdapter(another_photo);
        recyclerPoem.setAdapter(recyclerAdapter);
    }

    /***********************************Adapter**********************************/
    class MyRecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {

        ArrayList<ReadingPoem.Otherinfo> another_photo;

        public MyRecyclerAdapter(ArrayList<ReadingPoem.Otherinfo> another_photo) {
            this.another_photo = another_photo;
        }

        public void setAdapter(ArrayList<ReadingPoem.Otherinfo> another_photo) {
            this.another_photo = another_photo;
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_another_poem, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            ReadingPoem.Otherinfo otherinfo = another_photo.get(position);

            Glide.with(ReadingPoemActivity.this)
                    .load(otherinfo.photo)
                    .into(holder.another_poem_img);
            holder.itemView.setTag(otherinfo.idarticles);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ReadingPoemActivity.this, ReadingPoemActivity.class);
                    intent.putExtra("articles_id",holder.itemView.getTag().toString());
                    startActivity(intent);
                    finish();
                }
            });
        }

        @Override
        public int getItemCount() {
            return another_photo.size();
        }
    }


    /**********************************ViewHolder********************************/

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView another_poem_img;

        public MyViewHolder(View itemView) {
            super(itemView);
            another_poem_img = (ImageView)itemView.findViewById(R.id.another_poem_img);
        }
    }







    /***************************************작품 담기 다이얼로그***********************************************/

    public class AddWorkDialog extends Dialog {

        private TextView text01, text02, text03;
        private TextView mLeftButton, mRightButton;
        String str01, str02, str03;
        private View.OnClickListener addDialog_leftListener, addDialog_rightListener;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // 다이얼로그 외부 화면 흐리게 표현
            WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
            lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            lpWindow.dimAmount = 0.8f;
            getWindow().setAttributes(lpWindow);

            //view mapping
            setContentView(R.layout.dialog_share);

            //findView
            text01 = (TextView) findViewById(R.id.text01);
            text02 = (TextView) findViewById(R.id.text02);
            text03 = (TextView) findViewById(R.id.text03);
            mLeftButton = (TextView) findViewById(R.id.btnMove);
            mRightButton = (TextView) findViewById(R.id.btnBack);

            // 제목과 내용을 생성자에서 셋팅
            text01.setText(str01);
            text02.setText(str02);
            text03.setText(str03);

            // 클릭 이벤트 셋팅
            mLeftButton.setOnClickListener(addDialog_leftListener);
            mRightButton.setOnClickListener(addDialog_rightListener);
        }


        // 생성자
        public AddWorkDialog(Context context, String str01, String str02, String str03,
                             View.OnClickListener addDialog_leftListener,
                             View.OnClickListener addDialog_rightListener) {
            super(context, android.R.style.Theme_Translucent_NoTitleBar);
            this.str01 = str01;
            this.str02 = str02;
            this.str03 = str03;
            this.addDialog_leftListener = addDialog_leftListener;
            this.addDialog_rightListener = addDialog_rightListener;
        }
    }

    /***************************************설정 다이얼로그***********************************************/

    public class SettingDialog extends Dialog {

        private TextView text02, text03;
        private String str02, str03;
        private View.OnClickListener settingDialog_listener02, settingDialog_listener03;
        private LinearLayout llRow02, llRow03;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // 다이얼로그 외부 화면 흐리게 표현
            WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
            lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            lpWindow.dimAmount = 0.8f;
            getWindow().setAttributes(lpWindow);

            //view mapping
            setContentView(R.layout.dialog_setting);

            //findView
            text02 = (TextView) findViewById(R.id.tv02);
            text03 = (TextView) findViewById(R.id.tv03);
            llRow02 = (LinearLayout) findViewById(R.id.llRow02);
            llRow03 = (LinearLayout) findViewById(R.id.llRow03);


            // 제목과 내용을 생성자에서 셋팅
            text02.setText(str02);
            text03.setText(str03);

            // 클릭 이벤트 셋팅
            llRow02.setOnClickListener(settingDialog_listener02);
            llRow03.setOnClickListener(settingDialog_listener03);
        }


        // 생성자
        public SettingDialog(Context context, String str02, String str03,
                             View.OnClickListener settingDialog_listener02,
                             View.OnClickListener settingDialog_listener03) {
            super(context, android.R.style.Theme_Translucent_NoTitleBar);
            this.str02 = str02;
            this.str03 = str03;
            this.settingDialog_listener02 = settingDialog_listener02;
            this.settingDialog_listener03 = settingDialog_listener03;
        }
    }

    /***************************************설정 다이얼로그02***********************************************/

    public class SettingDialog02 extends Dialog {

        private TextView text02;
        private String str02;
        private View.OnClickListener settingDialog_listener02;
        private LinearLayout llRow02;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // 다이얼로그 외부 화면 흐리게 표현
            WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
            lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            lpWindow.dimAmount = 0.8f;
            getWindow().setAttributes(lpWindow);

            //view mapping
            setContentView(R.layout.dialog_setting02);

            //findView
            text02 = (TextView) findViewById(R.id.tv02);
            llRow02 = (LinearLayout) findViewById(R.id.llRow02);


            // 제목과 내용을 생성자에서 셋팅
            text02.setText(str02);

            // 클릭 이벤트 셋팅
            llRow02.setOnClickListener(settingDialog_listener02);
        }


        // 생성자
        public SettingDialog02(Context context, String str02,
                               View.OnClickListener settingDialog_listener02) {
            super(context, android.R.style.Theme_Translucent_NoTitleBar);
            this.str02 = str02;
            this.settingDialog_listener02 = settingDialog_listener02;
        }
    }


    /***************************************상세정보 다이얼로그***********************************************/

    public class InfoDialog extends Dialog {


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // 다이얼로그 외부 화면 흐리게 표현
            WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
            lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            lpWindow.dimAmount = 0.8f;
            getWindow().setAttributes(lpWindow);

            //view mapping
            setContentView(R.layout.dialog_info);

        }


        // 생성자
        public InfoDialog(Context context) {
            super(context, android.R.style.Theme_Translucent_NoTitleBar);
        }
    }






}
