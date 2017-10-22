package com.seoulprojet.seoulpoem.activity;


import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.AddListData;
import com.seoulprojet.seoulpoem.model.AddResult;
import com.seoulprojet.seoulpoem.model.MyPageResult;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddActivity extends AppCompatActivity {


    //tool_bar
    private RelativeLayout rlHamberger;
    private TextView tvWorkNum;


    //recycler
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<AddListData> works;

    //네트워크
    NetworkService service;

    // drawer
    private ImageButton hamburger_setting_btn, hamburger_mypage_btn, hamburger_scrab_btn, hamburger_today_btn, hamburger_writer_btn, hamburger_notice_btn;
    private TextView hamburger_name, hamburger_message;
    private ImageView hamburger_profile, hamburger_bg;
    private View drawerView;
    private DrawerLayout drawerLayout;

    //유저 정보
    private String userEmail = null;
    private int loginType = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        //유저 정보 가져오기
        Intent intent = getIntent();
        userEmail = intent.getExtras().getString("userEmail");
        loginType = intent.getExtras().getInt("loginType");


        //서비스 객체 초기화
        service = ApplicationController.getInstance().getNetworkService();

        //findView
        findView();

        //layout manager setting
        recyclerView = (RecyclerView) findViewById(R.id.rvWorks);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        //어뎁터 생성, 리사이클러뷰에 붙임
        recyclerAdapter = new RecyclerAdapter(works);
        recyclerView.setAdapter(recyclerAdapter);

        //네트워킹
        works = new ArrayList<>();
        getWorks();


        // drawer
        showHamburger();


    }


    /***************************************findView***********************************************/
    public void findView() {
        rlHamberger = (RelativeLayout) findViewById(R.id.rlHamberger);
        tvWorkNum = (TextView) findViewById(R.id.tvWorkNum);
    }


    /***********************************Adapter**********************************/
    class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {

        ArrayList<AddListData> addListDatas;

        public RecyclerAdapter(ArrayList<AddListData> addListDatas) {
            this.addListDatas = addListDatas;
        }

        public void setAdapter(ArrayList<AddListData> addListDatas) {
            this.addListDatas = addListDatas;
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_work_add, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            final AddListData addListData = addListDatas.get(position);


            //사진 이미지
            Glide.with(getApplicationContext())
                    .load(addListData.photo)
                    .into(holder.ivLeftImg);

            //동그란 이미지
            Glide.with(getApplicationContext())
                    .load(addListData.profile)
                    .into(holder.ivCirclerImg);

            //title
            holder.tvTitle.setText(addListData.title);

            //content
            holder.tvContent.setText(addListData.content);


            //상세 프로필로 이동
            //클릭시 상세화면으로 이동, 클릭한 프로젝트 아이디 전달
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddActivity.this, DetailActivity.class);
                    intent.putExtra("articleId", addListData.idarticles);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return works != null ? works.size() : 0;
        }
    }


    /**********************************ViewHolder********************************/

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvContent;
        ImageView ivLeftImg, ivCirclerImg;


        public MyViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
            ivLeftImg = (ImageView) itemView.findViewById(R.id.ivLeftImg);
            ivCirclerImg = (ImageView) itemView.findViewById(R.id.ivCirclerImg);
        }
    }

    /***********************************작품 리스트 가져오기*********************************/
    public void getWorks() {
        Call<AddResult> requestWorkLists = service.getWorks(userEmail, loginType);

        requestWorkLists.enqueue(new Callback<AddResult>() {
            @Override
            public void onResponse(Call<AddResult> call, Response<AddResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().status.equals("success")) {
                        works = response.body().bookmark_list;
                        recyclerAdapter.setAdapter(works);

                        //작품 개수 설정
                        tvWorkNum.setText(String.valueOf(works.size()));


                    }
                }
            }


            @Override
            public void onFailure(Call<AddResult> call, Throwable t) {
                Log.i("err", t.getMessage());
            }
        });
    }


    /*************************************** drawer **********************************************/
    public void showHamburger() {

        hamburger_mypage_btn = (ImageButton) findViewById(R.id.hamburger_mypage_btn);
        hamburger_scrab_btn = (ImageButton) findViewById(R.id.hamburger_scrab_btn);
        hamburger_today_btn = (ImageButton) findViewById(R.id.hamburger_todayseoul_btn);
        hamburger_writer_btn = (ImageButton) findViewById(R.id.hamburger_writerlist_btn);
        hamburger_notice_btn = (ImageButton) findViewById(R.id.hamburger_notice_btn);
        hamburger_setting_btn = (ImageButton) findViewById(R.id.hamburger_setting_btn);
        hamburger_name = (TextView) findViewById(R.id.hamburger_name);
        hamburger_message = (TextView) findViewById(R.id.hamburger_message);
        hamburger_profile = (ImageView) findViewById(R.id.hamburger_profile_img);
        hamburger_bg = (ImageView) findViewById(R.id.hamburger_bg);

        drawerLayout = (DrawerLayout) findViewById(R.id.mypage_drawer_layout02);
        drawerView = findViewById(R.id.drawer);
        rlHamberger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //햄버거 내 정보 가져오기
                getMenuMypage();
                drawerLayout.openDrawer(drawerView);
            }
        });

        hamburger_mypage_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyPage.class);
                startActivity(intent);
            }
        });

        hamburger_scrab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddActivity.class);
                startActivity(intent);
            }
        });


        hamburger_today_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TodaySeoul.class);
                startActivity(intent);
                finish();
            }
        });

        hamburger_setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingPage.class);
                startActivity(intent);
                finish();
            }
        });

        hamburger_notice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                Intent intent = new Intent(getApplicationContext(), Notice.class);
                startActivity(intent);
                finish();
            }
        });

        hamburger_writer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WriterList.class);
                startActivity(intent);
                finish();
            }
        });
    }


    /*************************************** mypage 정보 가져오기 *************************************/
    public void getMenuMypage() {
        Call<MyPageResult> requestMyPage = service.getMyPage("godz33@naver.com", 1);

        requestMyPage.enqueue(new Callback<MyPageResult>() {
            @Override
            public void onResponse(Call<MyPageResult> call, Response<MyPageResult> response) {
                if (response.isSuccessful()) {
                    Log.d("error", "xxx");
                    if (response.body().status.equals("success")) {
                        hamburger_name.setText(response.body().msg.pen_name);
                        hamburger_message.setText(response.body().msg.inform);
                        Glide.with(getApplicationContext())
                                .load(response.body().msg.profile)
                                .into(hamburger_profile);
                        Glide.with(getApplicationContext())
                                .load(response.body().msg.background)
                                .into(hamburger_bg);
                    }
                }
            }

            @Override
            public void onFailure(Call<MyPageResult> call, Throwable t) {
                Log.i("mypage error", t.getMessage());
            }
        });
    }

}
