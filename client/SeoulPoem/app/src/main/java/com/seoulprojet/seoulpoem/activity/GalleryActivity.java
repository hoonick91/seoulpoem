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
    private RelativeLayout rlHamberger, rlPictures, rlSearch;
    private TextView tvPlaceName;

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private GridLayoutManager layoutManager;

    private ArrayList<GalleryListData> gallerys;


    //네트워크
    NetworkService service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);


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

        Log.d("test", "before method call");
        //네트워크
        getPhotos();
        Log.d("test", "after method call");

        //메인으로
        toMain();

        //검색
        toSearch();


    }


    /***************************************findView***********************************************/
    public void findView() {
        rlHamberger = (RelativeLayout) findViewById(R.id.rlHamberger);
        rlPictures = (RelativeLayout) findViewById(R.id.rlPictures);
        rlSearch = (RelativeLayout) findViewById(R.id.rlSearch);
        tvPlaceName = (TextView) findViewById(R.id.tvPlaceName);

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

    /**********************************to main**********************************/
    public void toMain() {
        rlPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //main 이동
                Intent intent = new Intent(GalleryActivity.this, MainActivity.class);
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


    /***********************************갤러리 리스트 가져오기*********************************/
    public void getPhotos() {
        Call<GalleryResult> requestGalleryLists = service.getPhotos("그리움");
        Log.d("test", "before galley call");
        requestGalleryLists.enqueue(new Callback<GalleryResult>() {
            @Override
            public void onResponse(Call<GalleryResult> call, Response<GalleryResult> response) {
                Log.d("test", "before galley success");
                if (response.isSuccessful()) {
                    if (response.body().sucess.equals("success")) {
                        Log.d("test", "after galley success");
                        gallerys = response.body().data;
                        Log.d("tag", gallerys.get(0).photo);
                        Log.d("tag", "fsddfa");
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


}
