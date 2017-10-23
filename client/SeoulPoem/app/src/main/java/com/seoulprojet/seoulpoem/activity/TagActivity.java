package com.seoulprojet.seoulpoem.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.fragment.RootFragment;
import com.seoulprojet.seoulpoem.fragment.StaticFragment;

public class TagActivity extends AppCompatActivity {

    // For this example, only two pages
    /**************************************** 변수 ********************************/

    static final int NUM_ITEMS = 2;
    ViewPager mPager;
    SlidePagerAdapter mPagerAdapter;

    //유저 정보
    private String userEmail = null;
    private int loginType = 0;

    /************************************* onCreate *****************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        //유저 정보 가져오기
        Intent intent = getIntent();
        userEmail = intent.getExtras().getString("userEmail");
        loginType = intent.getExtras().getInt("loginType");


        //가져온 유저 정보 두번째 프레그먼트에 전달
        StaticFragment staticFragment= new StaticFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userEmail", userEmail);
        bundle.putInt("loginType", loginType);
        staticFragment.setArguments(bundle);



        /* Instantiate a ViewPager and a PagerAdapter. */
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new SlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    /************************ PagerAdapter class ********************************/
    public class SlidePagerAdapter extends FragmentPagerAdapter {
        public SlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return new RootFragment();
            else
                return new StaticFragment();
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }
    }
}
