package com.seoulprojet.seoulpoem.activity;


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
import android.widget.TextView;
import android.widget.Toast;
import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.Poem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //tool_bar
    private ImageView ivHamberger, ivPictures, ivSearch;


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

    }


    /***************************************findView***********************************************/
    public void findView() {
        ivHamberger = (ImageView) findViewById(R.id.ivHamberger);
        ivPictures = (ImageView) findViewById(R.id.ivPictures);
        ivSearch = (ImageView) findViewById(R.id.ivSearch);
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

//            //클릭하면 상세로 이동
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(HomeActivity.this, EventDetailActivity.class);
//                    intent.putExtra("event_id", Integer.parseInt(view.getTag().toString()));
//                    startActivity(intent);
//                }
//            });

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

}
