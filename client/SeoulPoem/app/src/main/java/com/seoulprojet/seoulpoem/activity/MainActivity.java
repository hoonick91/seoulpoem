package com.seoulprojet.seoulpoem.activity;


import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.GalleryListData;
import com.seoulprojet.seoulpoem.model.Poem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //tool_bar
    private ImageView ivHamberger, ivPictures;
    private LinearLayout llsearch, llSearchButton;
    private RelativeLayout rlSearchResult;


    //viewpager
    private PagerContainer pcPoem;
    private ViewPager vpPoems;
    private PagerAdapter paPoem;
    private ArrayList<Poem> poems;
    private ImageView ivPoem;
    private TextView tvHashTag;
    private TabLayout tabLayout;


    //view pager 밑 부분
    private ImageView img01, img02, img03, img04, img05;
    private ImageButton ibUpload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //findView
        findView();

        //makeDummy
        makeDummy();

        //view pager 설정
        setViewPager();

        //업로드 하기 버튼
        clickUplaodButton();

        //겔러리도 이동
        moveToGallery();

        //검색
        search01();
        search02();

    }


    /***************************************findView***********************************************/
    public void findView() {
        ivHamberger = (ImageView) findViewById(R.id.ivHamberger);
        ivPictures = (ImageView) findViewById(R.id.ivPictures);
//        ivSearch = (ImageView) findViewById(R.id.ivSearch);
        llsearch = (LinearLayout) findViewById(R.id.llsearch);
        llSearchButton = (LinearLayout) findViewById(R.id.llSearchButton);
        rlSearchResult = (RelativeLayout) findViewById(R.id.rlSearchResult);
        vpPoems = (ViewPager) findViewById(R.id.vpPoems);
        img01 = (ImageView) findViewById(R.id.iv01);
        img02 = (ImageView) findViewById(R.id.iv02);
        img03 = (ImageView) findViewById(R.id.iv03);
        img04 = (ImageView) findViewById(R.id.iv04);
        img05 = (ImageView) findViewById(R.id.iv05);
        ibUpload = (ImageButton) findViewById(R.id.ibUpload);
    }

    /************************************더미 데이터 생성*****************************************/
    private void makeDummy() {
        poems = new ArrayList<>();
        poems.add(new Poem(R.drawable.testimg, "#봄"));
        poems.add(new Poem(R.drawable.testimg02, "#여름"));
        poems.add(new Poem(R.drawable.testimg03, "#가을"));
        poems.add(new Poem(R.drawable.testimg04, "#겨울"));
        poems.add(new Poem(R.drawable.testimg05, "#계절"));
    }

    /***********************************peom Adapter**********************************/
    public class PageAdapterPoems extends PagerAdapter {

        ArrayList<Poem> poems;


        public PageAdapterPoems(ArrayList<Poem> poems) {
            this.poems = poems;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            final View view = LayoutInflater.from(container.getContext()).inflate(R.layout.list_item_poem_home, container, false);

            Poem poem = poems.get(position);

            //findView
            ivPoem = (ImageView) view.findViewById(R.id.ivPoem);
            tvHashTag = (TextView) view.findViewById(R.id.tvHashTag);


            ivPoem.setImageResource(poem.img);
            tvHashTag.setText(poem.hashTag.toString());
//            //화면 배치
//            Glide.with(HomeActivity.this)
//                    .load(event.photo)
//                    .into(ivEvent);
//            view.setTag(event.id);
//            tvGroupName.setText(event.group_title);
//            tvEventName.setText(event.event_title);
//            tvEventIntro.setText(event.text);

            //클릭하면 상세로 이동
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
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
    public void setViewPager(){
        pcPoem = (PagerContainer) findViewById(R.id.pcPoem);
        vpPoems = pcPoem.getViewPager();
        paPoem = new PageAdapterPoems(poems);
        vpPoems.setAdapter(paPoem);
        vpPoems.setOffscreenPageLimit(paPoem.getCount());
        vpPoems.setPageMargin(12);
        vpPoems.setClipChildren(false);
        vpPoems.setCurrentItem(2);

        //indicator 설정
        tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(vpPoems, true);

        //view pager 밑 이미지 세팅
        img01.setImageResource(poems.get(0).img);
        img02.setImageResource(poems.get(1).img);
        img03.setImageResource(poems.get(2).img);
        img04.setImageResource(poems.get(3).img);
        img05.setImageResource(poems.get(4).img);
    }


    /***********************************upload button**********************************/
    public void clickUplaodButton(){
        ibUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "업로드로 이동", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**********************************gallery move**********************************/
    public void moveToGallery(){
        ivPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //갤러리로 이동
                Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    /**********************************search 01**********************************/
    public void search01(){
        llsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //tool bar invisible
                LinearLayout llTbMain = (LinearLayout) findViewById(R.id.icToolbar);
                llTbMain.setVisibility(v.INVISIBLE);

                //search bar visible
                RelativeLayout llSearchBar = (RelativeLayout) findViewById(R.id.rlSearchBar);
                llSearchBar.setVisibility(v.VISIBLE);


                //full gray screen visible
                LinearLayout llbg = (LinearLayout) findViewById(R.id.llbg);
                llbg.setVisibility(v.VISIBLE);
            }
        });
    }

    /**********************************search 02**********************************/
    public void search02(){
        llSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rlSearchResult.setVisibility(v.VISIBLE);
            }
        });
    }




}
