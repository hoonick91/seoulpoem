package com.seoulprojet.seoulpoem.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.GalleryListData;
import com.seoulprojet.seoulpoem.model.GalleryResult;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GalleryActivity extends AppCompatActivity {


    //tool_bar
    private RelativeLayout rlBack, rlSearch;
    private TextView tvPlaceName;

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private GridLayoutManager layoutManager;

    private ArrayList<GalleryListData> gallerys;

    private RelativeLayout rlToWrite;

    //유저 정보
    private String userEmail = null;
    private int loginType = 0;

    //네트워크
    NetworkService service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        //유저 정보 가져오기
        Intent intent = getIntent();
        userEmail = intent.getExtras().getString("userEmail");
        loginType = intent.getExtras().getInt("loginType");


        //서비스 객체 초기화
        service = ApplicationController.getInstance().getNetworkService();

        //findView
        findView();


        //레이아웃 매니저 설정
        recyclerView = (RecyclerView) findViewById(R.id.rvGallery);
        layoutManager = new GridLayoutManager(GalleryActivity.this, 3);
        recyclerView.setLayoutManager(layoutManager);

        gallerys = new ArrayList<>();

        //어뎁터 생성, 리사이클러뷰에 붙임
        recyclerAdapter = new RecyclerAdapter(gallerys);
        recyclerView.setAdapter(recyclerAdapter);

        //네트워크
        getPhotos();

        //메인으로
        toMain();

        //검색으로
        toSearch();

        //작성하기로
        toWrite();


    }


    /***************************************findView***********************************************/
    public void findView() {
        rlBack = (RelativeLayout) findViewById(R.id.rlBack);
        rlSearch = (RelativeLayout) findViewById(R.id.rlSearch);
        tvPlaceName = (TextView) findViewById(R.id.tvPlaceName);
        rlToWrite = (RelativeLayout) findViewById(R.id.rlToWrite);

    }


    /***********************************Adapter**********************************/
    class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {

        ArrayList<GalleryListData> galleryListDatas;


        public RecyclerAdapter(ArrayList<GalleryListData> galleryListDatas) {
            this.galleryListDatas = galleryListDatas;
        }

        public void setAdapter(ArrayList<GalleryListData> galleryListDatas) {
            this.galleryListDatas = galleryListDatas;
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_photo, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final GalleryListData galleryListData = galleryListDatas.get(position);

            Glide.with(getApplicationContext())
                    .load(galleryListData.photo)
                    .into(holder.ivPhoto);

            //detail 화면으로 이동
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(GalleryActivity.this, DetailActivity.class);
                    intent.putExtra("articleId", galleryListData.idarticles);
                    intent.putExtra("userEmail", userEmail);
                    intent.putExtra("loginType", loginType);
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return gallerys.size();
        }
    }


    /**********************************ViewHolder********************************/
    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivPhoto;

        public MyViewHolder(View itemView) {
            super(itemView);

            ivPhoto = (ImageView) itemView.findViewById(R.id.ivPhoto);
        }
    }


    /***********************************갤러리 리스트 가져오기*********************************/
    public void getPhotos() {
        Call<GalleryResult> requestGalleryLists = service.getPhotos("그리움");
        requestGalleryLists.enqueue(new Callback<GalleryResult>() {
            @Override
            public void onResponse(Call<GalleryResult> call, Response<GalleryResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().sucess.equals("success")) {
                        gallerys = response.body().data;
                        recyclerAdapter.setAdapter(gallerys);
                    }
                }
            }


            @Override
            public void onFailure(Call<GalleryResult> call, Throwable t) {
                Log.i("err", t.getMessage());
            }
        });
    }


    /**********************************to main**********************************/
    public void toMain() {
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //main 이동
                Intent intent = new Intent(GalleryActivity.this, MainActivity.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                startActivity(intent);
                finish();
            }
        });
    }


    /**********************************search**********************************/
    public void toSearch() {
        rlSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //갤러리로 이동
                Intent intent = new Intent(GalleryActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    /**********************************Write**********************************/
    public void toWrite() {
        rlToWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //작성하기로 이동
                //Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                //startActivity(intent);
                Toast.makeText(GalleryActivity.this, "call write activity", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
