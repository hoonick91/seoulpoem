package com.seoulprojet.seoulpoem.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.MyPageResult;
import com.seoulprojet.seoulpoem.model.WriterApplyResult;
import com.seoulprojet.seoulpoem.model.WriterListResult;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WriterList extends AppCompatActivity {

    private String userEmail = null;
    private int loginType = 0;

    private ImageButton writerlist_hamburger_btn;
    private ImageButton writerlist_apply_btn;

    private TextView writerNum_tv;

    // drawer 선언
    private ImageButton hamburger_setting_btn, hamburger_mypage_btn, hamburger_scrab_btn, hamburger_today_btn, hamburger_writer_btn,hamburger_notice_btn;
    private TextView hamburger_name, hamburger_message;
    private ImageView hamburger_profile, hamburger_bg;
    private View drawerView;
    private DrawerLayout drawerLayout;

    // network
    NetworkService service;
    private ArrayList<MyPageResult> myPageResults;
    private ArrayList<WriterListResult.AuthorList> authorLists;

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writer_list);

        Intent intent = getIntent();
        userEmail = intent.getStringExtra("userEmail");
        loginType = intent.getExtras().getInt("loginType");

        // network
        service = ApplicationController.getInstance().getNetworkService();
        authorLists = new ArrayList<>();
        getWriterList();

        // 초기화
        writerlist_hamburger_btn = (ImageButton)findViewById(R.id.writerlist_hamburger_btn);
        writerlist_apply_btn = (ImageButton)findViewById(R.id.writerlist_apply_btn);
        writerNum_tv = (TextView)findViewById(R.id.writerlist_writer_num);

        // drawer
        drawerLayout = (DrawerLayout)findViewById(R.id.writerlist_drawer_layout);
        drawerView = (View)findViewById(R.id.drawer);
        writerlist_hamburger_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getMenuMypage();
                showHamburger();
                drawerLayout.openDrawer(drawerView);
            }
        });

        // dialog
        writerlist_apply_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                postWriterApply();
            }
        });

        //layout manager setting
        recyclerView = (RecyclerView)findViewById(R.id.writerlist_rv);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);



    }

    /**********Adapter***********/
    class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder>{

        ArrayList<WriterListResult.AuthorList> writerListDatas;


        public RecyclerAdapter(ArrayList<WriterListResult.AuthorList> writerListDatas){
            this.writerListDatas = writerListDatas;
        }

        public void setAdapter(ArrayList<WriterListResult.AuthorList> writerListDatas){
            this.writerListDatas = writerListDatas;
            notifyDataSetChanged();
        }

        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_writerlist, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            WriterListResult.AuthorList writerListData = writerListDatas.get(position);

            if(writerListData.profile == null){
                holder.profImg.setImageResource(R.drawable.profile_tmp);
            }

            else{
                Glide.with(getApplicationContext())
                        .load(writerListData.profile)
                        .into(holder.profImg);
            }

            // holder.profImg.setImageResource(writerListData.profile);
            holder.writerName_tv.setText(writerListData.pen_name);
            holder.writerMessage_tv.setText(writerListData.inform);
        }

        @Override
        public int getItemCount() {
            return writerListDatas != null ? writerListDatas.size() : 0;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView writerName_tv, writerMessage_tv;
        ImageView profImg;

        public MyViewHolder(View itemView){
            super(itemView);

            profImg = (ImageView)itemView.findViewById(R.id.item_writerlist_img);
            writerName_tv = (TextView)itemView.findViewById(R.id.item_writerlist_name_tv);
            writerMessage_tv = (TextView)itemView.findViewById(R.id.item_writerlist_message_tv);
        }
    }

    /********* drawer **********/
    public void showHamburger(){

        hamburger_mypage_btn = (ImageButton)findViewById(R.id.hamburger_mypage_btn);
        hamburger_scrab_btn = (ImageButton)findViewById(R.id.hamburger_scrab_btn);
        hamburger_today_btn = (ImageButton)findViewById(R.id.hamburger_todayseoul_btn);
        hamburger_writer_btn = (ImageButton)findViewById(R.id.hamburger_writerlist_btn);
        hamburger_notice_btn = (ImageButton)findViewById(R.id.hamburger_notice_btn);
        hamburger_setting_btn = (ImageButton)findViewById(R.id.hamburger_setting_btn);
        hamburger_name = (TextView)findViewById(R.id.hamburger_name);
        hamburger_message = (TextView)findViewById(R.id.hamburger_message);
        hamburger_profile = (ImageView)findViewById(R.id.hamburger_profile_img);
        hamburger_bg = (ImageView)findViewById(R.id.hamburger_bg);

        hamburger_scrab_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddActivity.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                startActivity(intent);
                finish();
            }
        });


        hamburger_mypage_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyPage.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                startActivity(intent);
                finish();
            }
        });

        hamburger_today_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TodaySeoul.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                startActivity(intent);
                finish();
            }
        });

        hamburger_setting_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingPage.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                startActivity(intent);
                finish();
            }
        });

        hamburger_notice_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                Intent intent = new Intent(getApplicationContext(), Notice.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                startActivity(intent);
                finish();
            }
        });

        hamburger_writer_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WriterList.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                startActivity(intent);
                finish();
            }
        });
    }

    /******************* mypage 정보 가져오기 ******************/
    public void getMenuMypage(){
        Call<MyPageResult> requestMyPage = service.getMyPage(userEmail, loginType);

        requestMyPage.enqueue(new Callback<MyPageResult>() {
            @Override
            public void onResponse(Call<MyPageResult> call, Response<MyPageResult> response) {
                if(response.isSuccessful()){
                    Log.d("error", "xxx");
                    if(response.body().status.equals("success")){
                        hamburger_name.setText(response.body().msg.pen_name);
                        hamburger_message.setText(response.body().msg.inform);

                        if(response.body().msg.profile == null){
                            hamburger_profile.setImageResource(R.drawable.profile_tmp);
                        }

                        else{
                            Glide.with(getApplicationContext())
                                    .load(response.body().msg.profile)
                                    .into(hamburger_profile);
                        }

                        if(response.body().msg.background == null){
                            hamburger_bg.setImageResource(R.drawable.profile_background);
                        }

                        else{
                            Glide.with(getApplicationContext())
                                    .load(response.body().msg.background)
                                    .into(hamburger_bg);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MyPageResult> call, Throwable t) {
                Log.i("mypage error", t.getMessage());
            }
        });
    }

    /********************** writer apply dialog *********************/
    private void showDialog(){
        LayoutInflater dialog = LayoutInflater.from(this);
        final View dialogLayout = dialog.inflate(R.layout.writer_apply_dialog, null);
        final Dialog applyDialog = new Dialog(this);

        applyDialog.setContentView(dialogLayout);
        applyDialog.show();

        ImageButton dialog_back_btn = (ImageButton)dialogLayout.findViewById(R.id.writerapply_dialog_back_btn);

        dialog_back_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                applyDialog.dismiss();
            }
        });
    }

    private void alreadyDialog(){
        LayoutInflater dialog = LayoutInflater.from(this);
        final View dialogLayout = dialog.inflate(R.layout.writer_apply_already_dialog, null);
        final Dialog alreadyDialog = new Dialog(this);

        alreadyDialog.setContentView(dialogLayout);
        alreadyDialog.show();

        ImageButton dialog_back_btn = (ImageButton)dialogLayout.findViewById(R.id.writerapply_already_dialog_back_btn);

        dialog_back_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                alreadyDialog.dismiss();
            }
        });
    }

    /******************* writer list ****************************/
    private void getWriterList(){
        Call<WriterListResult> requestList = service.getWriterList();

        requestList.enqueue(new Callback<WriterListResult>() {
            @Override
            public void onResponse(Call<WriterListResult> call, Response<WriterListResult> response) {

                if(response.isSuccessful()){
                    authorLists = response.body().authors_list;
                    writerNum_tv.setText("# 총 " + response.body().count_authors +"명");

                    // make adapter
                    recyclerAdapter = new RecyclerAdapter(authorLists);
                    recyclerView.setAdapter(recyclerAdapter);

                    Log.i("wirter list success", "");
                }
            }

            @Override
            public void onFailure(Call<WriterListResult> call, Throwable t) {
                Log.i("writer list error" , t.getMessage());
            }
        });
    }

    /***************** post writer apply **********************/
    private void postWriterApply(){
        Call<WriterApplyResult> requestApply = service.postWriterApply(userEmail);

        requestApply.enqueue(new Callback<WriterApplyResult>() {
            @Override
            public void onResponse(Call<WriterApplyResult> call, Response<WriterApplyResult> response) {

                if(response.code() == 403){
                    alreadyDialog();
                }
                else{
                    showDialog();
                }
            }

            @Override
            public void onFailure(Call<WriterApplyResult> call, Throwable t) {
                Log.i("fail call", t.getMessage());
            }
        });
    }
}
