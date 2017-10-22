package com.seoulprojet.seoulpoem.activity;


import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.HashtagListData;
import com.seoulprojet.seoulpoem.model.MainResult;
import com.seoulprojet.seoulpoem.model.MyPageResult;
import com.seoulprojet.seoulpoem.model.PoemListData;
import com.seoulprojet.seoulpoem.model.TestResult;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    /***************************************변수***********************************************/

    //tool_bar
    private RelativeLayout rlHamberger, rlSearch;
    private Toolbar tbMain;

    //hash tag
    private LinearLayout llHashTag;
    private RelativeLayout rlHashTagToggle;

    //main bg
    private RelativeLayout rlMainBg;

    //viewpager
    private PagerContainer pcPoem;
    private ViewPager vpPoems;
    private PagerAdapter paPoem;
    private ArrayList<PoemListData> poems;
    private ImageView ivPoem;
    private TextView tvHashTag;
    private TabLayout tabLayout;

    private RelativeLayout rlMore;


    //recycler
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<HashtagListData> hashtags;

    //to Write
    private RelativeLayout rlToWrite;

    //네트워크
    NetworkService service;

    // drawer
    private ImageButton hamburger_setting_btn, hamburger_mypage_btn, hamburger_scrab_btn, hamburger_today_btn, hamburger_writer_btn, hamburger_notice_btn;
    private TextView hamburger_name, hamburger_message;
    private ImageView hamburger_profile, hamburger_bg;
    private View drawerView;
    private DrawerLayout drawerLayout;

    //my page
    private TextView mypage_name_txt;
    private TextView mypage_message_txt;
    private ImageView mypage_profile_img;
    private ImageView mypage_bg_iv;


    /***************************************START***********************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //서비스 객체 초기화
        service = ApplicationController.getInstance().getNetworkService();


        //findView
        findView();

        //makeDummy
        makeDummy();

        //view pager 설정
        setViewPager();

        //햄버거 toggle
        //toHamberger();

        //검색
        toSearch();


        //recycler setting
        setRecycler();

        //작성하기로
        toWrite();


        //네트워킹
        poems = new ArrayList<>();
        getLists();


        // drawer
        showHamburger();
    }


    /******************* mypage 정보 가져오기 ******************/
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


    /***************************************findView***********************************************/
    public void findView() {

        rlMainBg = (RelativeLayout) findViewById(R.id.rlMainBg);
        tbMain = (Toolbar) findViewById(R.id.tbMain);
        rlHamberger = (RelativeLayout) findViewById(R.id.rlHamberger);
        rlSearch = (RelativeLayout) findViewById(R.id.rlSearch);

        llHashTag = (LinearLayout) findViewById(R.id.llHashTag);
        rlHashTagToggle = (RelativeLayout) findViewById(R.id.rlHashTagToggle);

        vpPoems = (ViewPager) findViewById(R.id.vpPoems);
        rlToWrite = (RelativeLayout) findViewById(R.id.rlToWrite);
        rlMore = (RelativeLayout) findViewById(R.id.rlMore);

        mypage_profile_img = (ImageView) findViewById(R.id.mypage_profile_img);
        mypage_name_txt = (TextView) findViewById(R.id.mypage_name_txt);
        mypage_message_txt = (TextView) findViewById(R.id.mypage_message_txt);
        mypage_bg_iv = (ImageView) findViewById(R.id.mypage_bg_iv);

    }


    /************************************더미 데이터 생성*****************************************/
    private void makeDummy() {

        //hash tag
        hashtags = new ArrayList<>();
        hashtags.add(new HashtagListData(R.drawable.testimg, "거리"));
        hashtags.add(new HashtagListData(R.drawable.testimg02, "이벤트"));
        hashtags.add(new HashtagListData(R.drawable.testimg03, "푸드"));
        hashtags.add(new HashtagListData(R.drawable.testimg04, "쇼핑"));
        hashtags.add(new HashtagListData(R.drawable.testimg05, "기타"));
        hashtags.add(new HashtagListData(R.drawable.testimg, "거리"));
        hashtags.add(new HashtagListData(R.drawable.testimg02, "이벤트"));
        hashtags.add(new HashtagListData(R.drawable.testimg03, "푸드"));
        hashtags.add(new HashtagListData(R.drawable.testimg04, "쇼핑"));
        hashtags.add(new HashtagListData(R.drawable.testimg05, "기타"));

    }

    /***********************************poem Adapter**********************************/
    public class PageAdapterPoems extends PagerAdapter {

        ArrayList<PoemListData> poemListDatas;


        public PageAdapterPoems(ArrayList<PoemListData> poemListDatas) {
            this.poemListDatas = poemListDatas;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            final View view = LayoutInflater.from(container.getContext()).inflate(R.layout.list_item_poem_home, container, false);

            final PoemListData poemListData = poemListDatas.get(position);

            //findView
            ivPoem = (ImageView) view.findViewById(R.id.ivPoem);
            tvHashTag = (TextView) view.findViewById(R.id.tvHashTag);


            Glide.with(getApplicationContext())
                    .load(poemListData.photo)
                    .into(ivPoem);


            tvHashTag.setText(poemListData.title.toString());

            //클릭하면 상세로 이동
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtra("articleId", poemListData.idarticles);
                    startActivity(intent);
                }
            });

            container.addView(view);
            return view;
        }


        @Override
        public float getPageWidth(int position) {
            return 1.0f;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return poems.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }
    }


    /***********************************view pager setting**********************************/
    public void setViewPager() {
        pcPoem = (PagerContainer) findViewById(R.id.pcPoem);
        vpPoems = pcPoem.getViewPager();


        //indicator 설정
        tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(vpPoems, true);

    }

    /***********************************recycler setting**********************************/
    public void setRecycler() {
        //layout manager setting
        recyclerView = (RecyclerView) findViewById(R.id.rvHashtags);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        //어뎁터 생성, 리사이클러뷰에 붙임
        recyclerAdapter = new RecyclerAdapter(hashtags);
        recyclerView.setAdapter(recyclerAdapter);
    }


    /***********************************Adapter**********************************/
    class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {

        ArrayList<HashtagListData> hashtagListDatas;

        public RecyclerAdapter(ArrayList<HashtagListData> hashtagListDatas) {
            this.hashtagListDatas = hashtagListDatas;
        }

        public void setAdapter(ArrayList<HashtagListData> hashtagListDatas) {
            this.hashtagListDatas = hashtagListDatas;
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_hashtag, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            HashtagListData hashtagListData = hashtagListDatas.get(position);


            //title
            holder.tvTitle.setText(hashtagListData.text);

            //img
            holder.ivHashtag.setImageResource(hashtagListData.imgResourceID);


//            //상세 프로필로 이동
//            //클릭시 상세화면으로 이동, 클릭한 프로젝트 아이디 전달
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(PeopleActivity.this, OtherUserPage.class);
//                    intent.putExtra("userID", Integer.parseInt(holder.tvUserID.getText().toString()));
//                    Log.d("userID", "people목록에서 보내는 id 값 : " + Integer.toString(userID));
//                    startActivity(intent);
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return hashtags != null ? hashtags.size() : 0;
        }
    }


    /**********************************ViewHolder********************************/

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        ImageView ivHashtag;


        public MyViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            ivHashtag = (ImageView) itemView.findViewById(R.id.ivHashtag);
        }
    }


    /***********************************main 리스트 가져오기*********************************/
    public void getLists() {
        Call<MainResult> requestMainLists = service.getPoems("그리움");
        Log.d("test", "before call");

        requestMainLists.enqueue(new Callback<MainResult>() {
            @Override
            public void onResponse(Call<MainResult> call, Response<MainResult> response) {
                Log.d("test", "after call");
                if (response.isSuccessful()) {
                    if (response.body().status.equals("success")) {
                        poems = response.body().data;

                        Log.d("test", "after success");

                        //view pager 설정
                        paPoem = new PageAdapterPoems(poems);
                        vpPoems.setAdapter(paPoem);
                        vpPoems.setOffscreenPageLimit(paPoem.getCount());
                        vpPoems.setPageMargin(12);
                        vpPoems.setClipChildren(false);
                        vpPoems.setCurrentItem(2);

                    }
                } else {
                    Log.d("test", response.toString());
                }
            }


            @Override
            public void onFailure(Call<MainResult> call, Throwable t) {
                Log.i("test -> err : ", t.getMessage());
            }
        });
    }


    /**********************************search**********************************/
    public void toSearch() {
        rlSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //검색으로 이동
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
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
                Toast.makeText(MainActivity.this, "call write activity", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /********* drawer **********/
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


}
