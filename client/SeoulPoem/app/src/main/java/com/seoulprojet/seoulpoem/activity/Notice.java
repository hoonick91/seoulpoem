package com.seoulprojet.seoulpoem.activity;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.NoticeListData;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Notice extends AppCompatActivity {

    private ImageButton notice_hamburger_btn;

    private ImageButton hamburger_setting_btn, hamburger_mypage_btn, hamburger_scrab_btn, hamburger_today_btn, hamburger_writer_btn,hamburger_notice_btn;
    private View drawerView;
    private DrawerLayout drawerLayout;

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<NoticeListData> noticeListDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        notice_hamburger_btn = (ImageButton)findViewById(R.id.notice_hamburger_btn);

        /*************** drawer ***************/
        hamburger_mypage_btn = (ImageButton)findViewById(R.id.hamburger_mypage_btn);
        hamburger_scrab_btn = (ImageButton)findViewById(R.id.hamburger_scrab_btn);
        hamburger_today_btn = (ImageButton)findViewById(R.id.hamburger_todayseoul_btn);
        hamburger_writer_btn = (ImageButton)findViewById(R.id.hamburger_writerlist_btn);
        hamburger_notice_btn = (ImageButton)findViewById(R.id.hamburger_notice_btn);
        hamburger_setting_btn = (ImageButton)findViewById(R.id.hamburger_setting_btn);

        drawerLayout = (DrawerLayout)findViewById(R.id.notice_drawer_layout);
        drawerView = (View)findViewById(R.id.drawer);
        notice_hamburger_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });

        hamburger_mypage_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyPage.class);
                startActivity(intent);
                finish();
            }
        });

        hamburger_setting_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingPage.class);
                startActivity(intent);
                finish();
            }
        });

        hamburger_notice_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                Intent intent = new Intent(getApplicationContext(), Notice.class);
                startActivity(intent);
                finish();
            }
        });

        hamburger_writer_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WriterList.class);
                startActivity(intent);
                finish();
            }
        });

        hamburger_today_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TodaySeoul.class);
                startActivity(intent);
                finish();
            }
        });

        ///////////////////////////////////
        recyclerView = (RecyclerView)findViewById(R.id.notice_rv);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        makeDummy();

        /***********recycler view ************/
        recyclerAdapter = new RecyclerAdapter(noticeListDatas);
        recyclerView.setAdapter(recyclerAdapter);

    }

    /******************** make dummy *********************/
    public void makeDummy(){
        noticeListDatas = new ArrayList<>();

        noticeListDatas.add(new NoticeListData("공지사항", "안녕하세요"));
        noticeListDatas.add(new NoticeListData("테스트입니다", "테스트"));
        noticeListDatas.add(new NoticeListData("반갑습니다", "안녕~~~"));
    }

    /******************** Adapter ************************/
    class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder>{
        ArrayList<NoticeListData> noticeListDatas;

        public RecyclerAdapter(ArrayList<NoticeListData> noticeListDatas){
            this.noticeListDatas = noticeListDatas;
        }

        public void setAdapter(ArrayList<NoticeListData> noticeListDatas){
            this.noticeListDatas = noticeListDatas;
            notifyDataSetChanged();
        }

        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_notice, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            NoticeListData noticeListData = noticeListDatas.get(position);

            holder.noticeTitle_tv.setText(noticeListData.noticeTitle);
            holder.noticeContent_tv.setText(noticeListData.noticeContent);
        }

        @Override
        public int getItemCount() {
            return noticeListDatas != null ? noticeListDatas.size() : 0;
        }
    }


    /******************** ViewHolder *********************/
    class MyViewHolder extends  RecyclerView.ViewHolder{
        TextView noticeTitle_tv, noticeContent_tv;

        public MyViewHolder(View itemView){
            super(itemView);

            noticeTitle_tv = (TextView)itemView.findViewById(R.id.item_notice_title_t);
            noticeContent_tv = (TextView)itemView.findViewById(R.id.item_noticecontent_t);
        }
    }
}
