package com.seoulprojet.seoulpoem.activity;


import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.AddListData;
import com.seoulprojet.seoulpoem.model.AddResult;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddActivity extends AppCompatActivity {


    //tool_bar
    private ImageView ivHamberger;
    private TextView tvWorkNum;


    //recycler
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<AddListData> works;

    //네트워크
    NetworkService service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

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


    }


    /***************************************findView***********************************************/
    public void findView() {
        ivHamberger = (ImageView) findViewById(R.id.ivHamberger);
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
        Call<AddResult> requestWorkLists = service.getWorks("godz33@naver.com", 1);

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

}
