package com.seoulprojet.seoulpoem.activity;

import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.Poem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //tool_bar
    private ImageView ivHamberger, ivPictures, ivSearch;

    //view pager
    private ViewPager vpPoems;
    private PagerContainer pcPoems;
    private PagerAdapter paPoems;
    private ArrayList<Poem> Poems;

    //view pager 밑 부분
    private ImageView img01, img02, img03, img04, img05;
    private ImageButton ibUpload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //findView
        findView();

        //view pager
        initViewPager();
    }



    /***************************************findView***********************************************/
    public void findView(){
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


    /***************************************viewPager***********************************************/
    public void initViewPager(){
        pcPoems = (PagerContainer) findViewById(R.id.pcPoems);
        vpPoems = pcPoems.getViewPager();
        paPoems = new PageAdapterRecentEvents(events);
        vpRecentEvents.setAdapter(paRecentEvents);
        vpRecentEvents.setOffscreenPageLimit(paRecentEvents.getCount());
        vpRecentEvents.setPageMargin(12);
        vpRecentEvents.setClipChildren(false);
        vpRecentEvents.setCurrentItem(1);
    }


    /******************************************최근행사 어뎁터*****************************************/
    public class PageAdapterPoems extends PagerAdapter {

        ArrayList<Poem> poems;

        public PageAdapterPoems(ArrayList<Poem> poems) {
            this.poems = poems ;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final View view = LayoutInflater.from(container.getContext()).inflate(R.layout.list_item_poem_home, container, false);

            Poem poem = poems.get(position);

            //findView
            ivEvent = (ImageView) view.findViewById(R.id.ivEvent);
            tvGroupName = (TextView) view.findViewById(R.id.tvGroupName);
            tvEventName = (TextView) view.findViewById(R.id.tvEventName);
            tvEventIntro = (TextView) view.findViewById(R.id.tvEventIntro);

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
            return poems();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }
    }





}
